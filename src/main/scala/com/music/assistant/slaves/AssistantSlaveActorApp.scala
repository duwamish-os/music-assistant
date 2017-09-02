package com.music.assistant.slaves

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object AssistantSlaveActorApp {

  def main(args: Array[String]): Unit = {

    println("sample port 2551")
    val port = if (args.isEmpty) "0" else args(0)

    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
      .withFallback(ConfigFactory.load())

    val actorSystem = ActorSystem("ServerCluster", config)
    actorSystem.actorOf(Props(classOf[AssistanceBackendActor]), name = "Backend")

    System.in.read()

    actorSystem.terminate()

  }

}
