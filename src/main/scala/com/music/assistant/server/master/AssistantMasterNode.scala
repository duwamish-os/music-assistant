package com.music.assistant.server.master

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}
import com.music.assistant.server.{AssistMeEvent, AssistMeResponseNotification, JobFailed, SlaveRegistration}
import com.typesafe.config.ConfigFactory

class AssistantMasterNode extends Actor {

  var slaveWorkers = IndexedSeq.empty[ActorRef]
  var slaveCounter = 0

  override def preStart(): Unit = println("[INFO]" + context.self.path)

  def receive = {

    case event: AssistMeEvent if slaveWorkers.isEmpty =>
      println("-------------------------------------------------------------------------------------")
      println("[INFO] AssistantMasterActor received event " + event + ", but no slaves have joined")
      println("-------------------------------------------------------------------------------------")
      sender() ! JobFailed("Service unavailable, try again later", event)

    case queryEvent: AssistMeEvent =>
      //validate the request
      slaveCounter += 1
      slaveWorkers(slaveCounter % slaveWorkers.size) forward queryEvent

    case SlaveRegistration if !slaveWorkers.contains(sender()) =>
      context watch sender()
      slaveWorkers = slaveWorkers :+ sender()

    case Terminated(a) => slaveWorkers = slaveWorkers.filterNot(_ == a)

  }
}

object AssistantMasterNode {

  def main(consoleArgs: Array[String]): Unit = {

    println("sample port 2551")

    val port = if (consoleArgs.isEmpty) "0" else consoleArgs(0)

    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]"))
      .withFallback(ConfigFactory.load())

    val actorSystem = ActorSystem("ServerCluster", config)
    val masterNode = actorSystem.actorOf(Props(classOf[AssistantMasterNode]), name = "master")

    //wait for input to terminate
    System.in.read()

    actorSystem.terminate()

  }

}
