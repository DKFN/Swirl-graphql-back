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

  def graphql(): Action[AnyContent] = Action {
    val query: Document =
      graphql"""
    query MyProduct {
      product(id: "2") {
        name
        description

        picture(size: 500) {
          width, height, url
        }
      }

      products {
        name
      }
    }
  """
    val res: Future[(Any, Any)] = Executor.execute(
        SchemaType.schema, query, new MovieRepository(bsClient)
      ).map(OK -> _)
      .recover {
        case error: QueryAnalysisError => BadRequest -> error.resolveError
        case error: ErrorWithResolver => InternalServerError -> error.resolveError
      }

    Ok("KK")
  }
}
