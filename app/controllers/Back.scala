package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import models.{MovieRepository, SchemaType}
import play.api.Configuration
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._
import sangria.ast.Document
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.macros._
import sangria.renderer.SchemaRenderer
import sangria.marshalling.playJson._
import sangria.parser.QueryParser
import sangria.ast.Document
import scala.util._

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

  def graphql(): Action[AnyContent] = Action.async { implicit request =>
    val res = QueryParser.parse(request.body.asText.getOrElse("")) match {
      case Success(qryAst: Document) => subExecutor(qryAst)
      case Failure(err) => Future.successful(Json.toJson("Cannot parse query ast build failed"))
    }
    res.map(x => Ok(Json.toJson(x)).withHeaders(
            "Access-Control-Allow-Origin" -> "*" 
     )
   )
  }

  def subExecutor(qry: Document) =
    Executor.execute(
      SchemaType.schema,
      qry,
      new MovieRepository (bsClient)
    ).recover {
      case error: QueryAnalysisError => Json.toJson(error.getMessage)
      case error: ErrorWithResolver => Json.toJson(error.getMessage)
    }
}
