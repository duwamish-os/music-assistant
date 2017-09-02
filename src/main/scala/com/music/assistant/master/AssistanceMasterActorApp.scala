package com.music.assistant.master

import akka.actor.{ActorRef, ActorSystem, Props}
import com.music.assistant.AssistMeEvent
import com.typesafe.config.ConfigFactory

import scala.io.StdIn
import scala.util.Random

object AssistanceMasterActorApp {

  def main(args: Array[String]): Unit = {

    println("sample port 2551")

    val port = if (args.isEmpty) "0" else args(0)

    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]")).
      withFallback(ConfigFactory.load())

    val actorSystem = ActorSystem("ServerCluster", config)
    val masterActor: ActorRef = actorSystem.actorOf(Props(classOf[AssistantMasterActor]), name = "Frontend")

    import scala.concurrent.duration._
    implicit val executionContext = actorSystem.dispatcher

    println("[INFO] keep sending events to master actor")

    actorSystem.scheduler.schedule(5.seconds, 10.seconds) {

      masterActor ! AssistMeEvent("Hi Siri, where is my \"To The Bone album\"?", "someuser-" + Random.nextInt(100))

    }

    System.in.read()

    actorSystem.terminate()

  }

}
