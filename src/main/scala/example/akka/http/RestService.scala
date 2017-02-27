package example.akka.http

/**
  * Created by phanther on 2/26/17.
  */
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

final case class CustomerInput(id: Int)
final case class CustomerOutput(id: Int, name: String, phone: String)

trait MyJsonResponse extends SprayJsonSupport {
  implicit val customerJsonInput = jsonFormat1(CustomerInput)
  implicit val customerJsonResponse = jsonFormat3(CustomerOutput)
}

trait RestService extends MyJsonResponse {
  implicit val actorSystem: ActorSystem
  implicit val actorMaterializer: ActorMaterializer

  val actorHandler = actorSystem.actorOf(ActorHandler.props(), "actorHandler")
  val route: Route = {
    implicit lazy val timeout = Timeout(1.seconds)
    path("customer") {
      post {
        entity(as[CustomerInput]) { inp: CustomerInput =>
          onSuccess(actorHandler ? inp) {
            case response: CustomerOutput => complete(StatusCodes.OK, response)
          }
        }
      }
    }
  }
}