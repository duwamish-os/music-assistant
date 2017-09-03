package com.music.assistant.server.master

import akka.actor.{Actor, ActorRef, Terminated}
import com.music.assistant.server.{AssistMeEvent, AssistMeResponseNotification, JobFailed, SlaveRegistration}

class AssistantMasterNode extends Actor {

  var slaveWorkers = IndexedSeq.empty[ActorRef]
  var slaveCounter = 0

  override def preStart(): Unit = println("[INFO]" + context.self.path)

  def receive = {

    case event: AssistMeEvent if slaveWorkers.isEmpty =>
      println("====================================================================================")
      println("[INFO] AssistantMasterActor received event " + event + ", but no slaves have joined")
      println("====================================================================================")
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
