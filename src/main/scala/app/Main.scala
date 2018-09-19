package app

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object Main {
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      startup("server", "2551", "2552", "0")
    } else {
      startup(args.head, args.tail: _*)
    }
  }

  def startup(role: String, ports: String*): Unit = {
    val system = ports.map { port =>
      val config = ConfigFactory.parseString(
        s"""
           |akka.remote.netty.tcp.port=$port
           |akka.remote.artery.canonical.port=$port
           |akka.cluster.roles=[$role]
           |""".stripMargin).withFallback(ConfigFactory.load())
      ActorSystem("ClusterSystem", config)
    }.last

    if (role == "server") {
      system.actorOf(Props[WebServer], "web-server")
    }
  }
}

case class Record(isPerson: Boolean, makeYear: Int, color: String,
                  brand: String, model: String, registrationNumber: String)