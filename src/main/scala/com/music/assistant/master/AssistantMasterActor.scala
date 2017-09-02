package com.music.assistant.master

import akka.actor.{Actor, ActorRef, Terminated}
import com.music.assistant.{AssistMeEvent, SlaveRegistration, JobFailed}

class AssistantMasterActor extends Actor {

  var slaveWorkers = IndexedSeq.empty[ActorRef]
  var jobCounter = 0

  def receive = {

    case event: AssistMeEvent if slaveWorkers.isEmpty =>
      println("[INFO] AssistantMasterActor received event " + event + ", but no slaves have joined")
      sender() ! JobFailed("Service unavailable, try again later", event)

    case queryEvent: AssistMeEvent =>
      jobCounter += 1
      slaveWorkers(jobCounter % slaveWorkers.size) forward queryEvent

    case SlaveRegistration if !slaveWorkers.contains(sender()) =>
      context watch sender()
      slaveWorkers = slaveWorkers :+ sender()

    case Terminated(a) =>
      slaveWorkers = slaveWorkers.filterNot(_ == a)

  }
}
