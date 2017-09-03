package com.music.assistant.server.slaves

import akka.actor.{Actor, RootActorPath}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.{Cluster, Member, MemberStatus}
import com.music.assistant.server.{AssistMeEvent, AssistMeResponseNotification, SlaveRegistration}

class AssistanceSlaveNode extends Actor {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, MemberUp
  // re-subscribe when restart
  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive : Actor.Receive = {

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
