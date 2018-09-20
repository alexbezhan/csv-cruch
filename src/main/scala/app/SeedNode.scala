package app

import akka.actor.ActorSystem

object SeedNode {
  def main(args: Array[String]): Unit = {
    val host = args(0)
    val port = args(1)
    ActorSystem("actor-system", Utils.loadConfig(host, port.toInt))
  }
}