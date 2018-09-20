package app

import java.io.File

import akka.actor.{Actor, ActorLogging, ActorRef}
import app.CsvService.{CountLines, LinesCount}

import scala.collection.mutable
import scala.io.Source

class CsvService(workersRouter: ActorRef) extends Actor() with ActorLogging {

  override def receive: Receive = {
    case CountLines =>
      val files = new File("csv").listFiles().map(_.getAbsolutePath())
      files.foreach { file => workersRouter ! ReadFileLinesCount(file) }
      context.become(collectingCounts(sender, mutable.Set(files: _*)))
  }

  private def collectingCounts(requester: ActorRef, files: mutable.Set[String]): Receive = {
    var acc: Int = 0
    return {
      case FileLinesCount(file, count) =>
        acc = acc + count
        files.remove(file)
        if (files.isEmpty) {
          context.unbecome()
          requester ! LinesCount(acc)
        }
    }
  }
}

object CsvService {

  case object CountLines

  case class LinesCount(count: Int)

}


private class CsvWorker extends Actor() with ActorLogging {

  override def preStart(): Unit = {
    log.info(s"CsvWorker is up at ${self.path}")
  }

  override def receive: Receive = {
    case ReadFileLinesCount(file) =>
      log.info(s"ReadFileLinesCount($file)")
      val size = Source.fromFile(file).getLines().size
      log.info(s"ReadFileLinesCount($file): $size")
      sender ! FileLinesCount(file, size)
  }
}

private case class FileLinesCount(file: String, count: Int)

private case class ReadFileLinesCount(file: String)