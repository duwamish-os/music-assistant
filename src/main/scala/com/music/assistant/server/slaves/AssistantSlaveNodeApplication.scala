package com.music.assistant.server.slaves

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object AssistantSlaveNodeApplication {

  def main(args: Array[String]): Unit = {

    println("sample port 2552")

    val port = if (args.isEmpty) "0" else args(0)

    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
      .withFallback(ConfigFactory.load())

    val actorSystem = ActorSystem("ServerCluster", config)
    actorSystem.actorOf(Props(classOf[AssistanceSlaveNode]), name = "worker")

    //wait for input to terminate
    System.in.read()

    actorSystem.terminate()

  }

}
