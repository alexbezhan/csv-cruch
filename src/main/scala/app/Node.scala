package app

import akka.actor.{ActorIdentity, ActorSystem, Identify, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.duration._
import scala.concurrent.ExecutionContextExecutor

object Node {
  def main(args: Array[String]): Unit = {
    val host = args(0)
    val port = args(1)
    val system = ActorSystem("actor-system", Utils.loadConfig(host, port.toInt))

    system.actorOf(ClusterSingletonManager.props(Props(new CsvService()), PoisonPill, ClusterSingletonManagerSettings(system)), "csv-service")
  }
}
