package app

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import app.CsvService.{CountLines, LinesCount}

import scala.concurrent.Future
import scala.concurrent.duration._

class WebHandler(csvService: ActorRef) extends Actor() with ActorLogging {

  import context.dispatcher

  private implicit val materializer: ActorMaterializer = ActorMaterializer()(context)
  private implicit val timeout: Timeout = Timeout(10 seconds)

  val route: Route =
    path("") {
      get {
        complete((csvService ? CountLines).mapTo[LinesCount].map(_.count.toString))
      }
    }

  private var binding: Future[Http.ServerBinding] = _

  override def preStart(): Unit = {
    val port = 8080
    binding = Http(context.system).bindAndHandle(route, "localhost", port)
    log.info(s"WebServer listening on port $port")
  }

  override def postStop(): Unit = {
    log.info("WebServer is stopping")
    binding.flatMap(_.unbind())
  }

  override def receive: Receive = Actor.emptyBehavior
}