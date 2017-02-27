package example.akka.http

/**
  * Created by phanther on 2/26/17.
  */
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import akka.http.scaladsl.Http

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

class HttpServer(implicit val actorSystem: ActorSystem,
                 implicit val actorMaterializer: ActorMaterializer,
                 implicit val executionContext: ExecutionContextExecutor)
  extends RestService {
  def startServer(host: String, port: Int) = {
    Http().bindAndHandle(route, host, port)
  }
}

object HttpServer extends App {
  implicit val actorSystem = ActorSystem("Rest-Server")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = actorSystem.dispatcher

  val server = new HttpServer()
  val conf = ConfigFactory.load()
  val host = conf.getString("akka.service.host") //.getOrElse("localhost")
  val port = conf.getInt("akka.service.port") //.getOrElse(8080)

  println("==========Starting Server==========")
  val bindingFuture = server.startServer(host, port)

  println(s"Host: $host -- Port: $port")
  println(s"Hit RETURN to terminate")
  StdIn.readLine()

  //Shutdown
  bindingFuture.flatMap(_.unbind())
  actorSystem.terminate()
}
