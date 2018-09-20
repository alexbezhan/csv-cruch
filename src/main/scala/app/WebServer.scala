package app

import akka.actor.{ActorSystem, Props}
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.ExecutionContextExecutor

object WebServer {
  def main(args: Array[String]): Unit = {
    val host = args(0)
    val port = args(1)
    val system = ActorSystem("actor-system", Utils.loadConfig(host, port.toInt))
    implicit val timeout: Timeout = Timeout(10 seconds)
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    val csvServiceRef = system.actorOf(ClusterSingletonProxy.props("/user/csv-service", ClusterSingletonProxySettings(system)), "csv-service-proxy")
    system.actorOf(Props(new WebHandler(csvServiceRef)), "web-handler")
  }
}