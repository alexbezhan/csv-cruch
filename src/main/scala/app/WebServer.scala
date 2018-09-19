package app

import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.Future

class WebServer extends Actor() with ActorLogging {

  import context.dispatcher

  implicit val materializer: ActorMaterializer = ActorMaterializer()(context)

  val route: Route =
    path("/") {
      get {
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Hello World"))
      }
    } ~
      path("/fetch") {
        get {
          parameter("link") { link =>
            complete(StatusCodes.OK, link)
          }
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