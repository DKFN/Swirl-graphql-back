package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import models.{MovieRepository, SchemaType}
import play.api.Configuration
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.json._
import play.api.mvc._
import sangria.ast.Document
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.macros._
import sangria.renderer.SchemaRenderer
import sangria.marshalling.playJson._

import scala.concurrent.ExecutionContext.Implicits.global
import services.BetaSeries

import scala.collection.immutable.ListMap
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
        "movie" -> (movie \ "movie").as[JsObject],
        "comments" -> (comments \ "comments").as[JsArray],
        "related" -> related
      ))
    }
  }

  def search(query: String): Action[AnyContent] = Action.async {
    bsClient.getSearch(query).map { x =>
      val queryContent = (x \ "movies").as[JsArray]
      Ok(queryContent)
    }
  }

  def schema() = Action {
    Ok(SchemaRenderer.renderSchema(SchemaType.schema))
  }

  def graphql(): Action[AnyContent] = Action.async {
    val query: Document =
      graphql"""
      query {
        movie(id: 135) {
          backdrop
          poster
          director
          title
        }
      }
  """
    val res: Future[JsValue] = Executor.execute(
      SchemaType.schema,
      query,
      new MovieRepository(bsClient)
    )/*.recover {
        case error: QueryAnalysisError => Json.toJson("error" -> error.getMessage)
        case error: ErrorWithResolver => Json.toJson("error" -> error.getMessage)
      }*/
    res.map(x => {
      Logger.info(x.toString)
      Ok(Json.toJson(x))
    })
  }
}
