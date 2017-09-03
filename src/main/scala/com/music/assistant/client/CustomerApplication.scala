package com.music.assistant.client

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{ActorSystem, Props}
import com.music.assistant.server.AssistMeEvent
import com.music.assistant.server.master.AssistantMasterNode
import com.typesafe.config.ConfigFactory

object CustomerApplication {

  def main(args: Array[String]): Unit = {

    import scala.concurrent.duration._
    import akka.util.Timeout
    implicit val duration: Timeout = 20 seconds

    //TODO invoke "/user/master" without having to specify port
    val actorSystem = ActorSystem()
    val masterActor = actorSystem.actorSelection("akka.tcp://ServerCluster@127.0.0.1:2551/user/master").resolveOne()

    import scala.concurrent.duration._
    implicit val executionContext = actorSystem.dispatcher

    val identifier = System.currentTimeMillis()

    println(s"[INFO] keep sending events to master actor ${identifier}")

    var userId = new AtomicInteger()


    for (actor <- masterActor) {
      actorSystem.scheduler.schedule(5.seconds, 10.seconds) {
        val i = userId.getAndIncrement()
        println(s"[INFO] querying by user-${i} ${actor.path}")
        actor ! AssistMeEvent("Hi Assistant, where is my \"To The Bone\" album?", identifier + "-someuser-" + i)
      }
    }
  }
}
