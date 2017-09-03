package com.music.assistant.server.master

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{ActorRef, ActorSystem, Props}
import com.music.assistant.server.AssistMeEvent
import com.typesafe.config.ConfigFactory

import scala.util.Random

object AssistantMasterNodeApplication {

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
