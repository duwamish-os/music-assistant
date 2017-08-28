package com.music.assistant.master

import akka.actor.{ActorRef, ActorSystem, Props}
import com.music.assistant.AssistMeJob
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object AssistanceMasterActorApp {

  def main(args: Array[String]): Unit = {

    println("sample port 2551")

    val port = StdIn.readInt()

    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]")).
      withFallback(ConfigFactory.load())

    val actorSystem = ActorSystem("ClusterSystem", config)
    val frontend: ActorRef = actorSystem.actorOf(Props(classOf[AssistanceMasterActor]), name = "Frontend")

    import scala.concurrent.duration._
    implicit val executionContext = actorSystem.dispatcher
    actorSystem.scheduler.schedule(5.seconds, 10.seconds) {
      frontend ! AssistMeJob("where is my To The Bone album?")
    }

    System.in.read()

    actorSystem.terminate()

  }

}
