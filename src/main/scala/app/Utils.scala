package app

import com.typesafe.config.{Config, ConfigFactory}

object Utils {
  def loadConfig(host: String, port: Int): Config =
    ConfigFactory.parseString(
      s"""|akka.remote.netty.tcp.hostname=$host
          |akka.remote.netty.tcp.port=$port
       """.stripMargin).withFallback(ConfigFactory.load())
}
