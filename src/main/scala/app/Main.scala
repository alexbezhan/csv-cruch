package app

import akka.actor.{ActorSystem, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.RoundRobinPool
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
      val workersRouter = {
        val pool = ClusterRouterPool(RoundRobinPool(0), ClusterRouterPoolSettings(10, 5, allowLocalRoutees = true))
        system.actorOf(pool.props(Props[CsvWorker]), "csv-workers-router")
      }
      val csvService = system.actorOf(Props(new CsvService(workersRouter)), "csv-service")
      system.actorOf(Props(new WebServer(csvService)), "web-server")
    }
  }
}

case class Record(isPerson: Boolean, makeYear: Int, color: String,
                  brand: String, model: String, registrationNumber: String)