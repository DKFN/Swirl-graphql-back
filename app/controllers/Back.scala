package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import play.api.Configuration
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import sangria.renderer.SchemaRenderer

import scala.concurrent.ExecutionContext.Implicits.global
import services.BetaSeries

import scala.concurrent.Future

class Back @Inject() (system: ActorSystem, config: Configuration, bsClient: BetaSeries) extends InjectedController {
  def index(): Action[AnyContent] = Action.async {
    bsClient.getInitMovies().flatMap(x => Future.successful(Ok(x)))
  }

  def getMovie(id: Int): Action[AnyContent] = Action.async {
    Logger.info(s"Getting movie id : ${id.toString}")
    for {
      movie <- bsClient.getMovie(id)
      comments <- bsClient.getComments(id)
      related <- bsClient.getRelated(id)
    } yield {
      Ok(Json.obj(
        "movie" -> movie,
        "comments" -> comments,
        "related" -> related
      ))
    }
  }
}