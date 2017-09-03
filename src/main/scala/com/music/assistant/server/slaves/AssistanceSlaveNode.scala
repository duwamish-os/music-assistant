package com.music.assistant.server.slaves

import akka.actor.{Actor, ActorSystem, Props, RootActorPath}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.{Cluster, Member, MemberStatus}
import com.music.assistant.server.{AssistMeEvent, AssistMeResponseNotification, SlaveRegistration}
import com.typesafe.config.ConfigFactory

class AssistanceSlaveNode extends Actor {

  val serverCluster = Cluster(context.system)

  // subscribe to cluster changes, MemberUp
  // re-subscribe when restart
  override def preStart(): Unit = serverCluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = serverCluster.unsubscribe(self)

  def receive: Actor.Receive = {

    case AssistMeEvent(query, userId) => {
      println(s"[INFO] AssistanceSlaveActor received ${query} from ${userId}")
      sender() ! AssistMeResponseNotification(query, "Some response", userId)
    }

    case state: CurrentClusterState =>
      println("[INFO] AssistanceSlaveActor CurrentClusterState")
      state.members.filter(_.status == MemberStatus.Up) foreach register

    case MemberUp(m) => register(m)
  }

  def register(member: Member): Unit = {
    println(s"[INFO] AssistanceSlaveActor register ${member}")
    if (member.hasRole("frontend"))
      context.actorSelection(RootActorPath(member.address) / "user" / "master") ! SlaveRegistration
  }
}

object AssistantSlaveNode {

  def main(args: Array[String]): Unit = {

    println("sample port 2552")

    val port = if (args.isEmpty) "0" else args(0)

    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
      .withFallback(ConfigFactory.load())

    val actorSystem = ActorSystem("ServerCluster", config)
    actorSystem.actorOf(Props(classOf[AssistanceSlaveNode]), name = "worker")

    //wait for input to terminate
    System.in.read()

    actorSystem.terminate()

  }

}
