package com.music.assistant.master

import akka.actor.{Actor, ActorRef, Terminated}
import com.music.assistant.{AssistMeJob, SlaveRegistration, JobFailed}

class AssistanceMasterActor extends Actor {

  var slaveWorkers = IndexedSeq.empty[ActorRef]
  var jobCounter = 0

  def receive = {

    case job: AssistMeJob if slaveWorkers.isEmpty =>
      println("[INFO] received command " + job.text)
      sender() ! JobFailed("Service unavailable, try again later", job)

    case job: AssistMeJob =>
      jobCounter += 1
      slaveWorkers(jobCounter % slaveWorkers.size) forward job

    case SlaveRegistration if !slaveWorkers.contains(sender()) =>
      context watch sender()
      slaveWorkers = slaveWorkers :+ sender()

    case Terminated(a) =>
      slaveWorkers = slaveWorkers.filterNot(_ == a)

  }
}
