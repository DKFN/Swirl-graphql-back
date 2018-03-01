package services

import javax.inject.Inject

import com.typesafe.play.cachecontrol.ResponseSelectionActions.GatewayTimeout
import models.Movie
import play.api.{Configuration, Logger}
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.libs.concurrent.Futures._
import play.api.libs.json.JsResult.Exception

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class BetaSeries @Inject() (client: WSClient, config: Configuration) {

  /**
    * Homepage movies
    */
  def getInitMovies(nb: Int = 50, offset: Int = 0): Future[JsValue] = {
    get(
      "movies/discover",
      Set(
        "type" -> "popular",
        "offset" -> offset.toString,
        "limit" -> nb.toString
      )
    ).map(x => handleResponse(x))
  }

  /**
    * Get movie details
    */
  def getMovie(id: Int): Future[JsValue] = {
    get("movies/movie",
      Set(
        "id" -> id.toString
      )
    ).map(x => handleResponse(x))
  }

  def getComments(id: Int): Future[JsValue] = {
    get("comments/comments",
      Set(
        "id" -> id.toString,
        "type" -> "movie",
        "nbpp" -> "20"
      )
   ).map(x => handleResponse(x))
  }

  def getRelated(id: Int): Future[JsValue] = {
    get("movies/similars",
      Set(
        "id" -> id.toString,
        "type" -> "movie"
      )
    ).map(x => handleResponse(x))
  }

  def getSearch(query: String): Future[JsValue] = {
    get("search/all",
      Set(
        "query" -> query,
        "limit" -> "20"
      )
    ).map(x => handleResponse(x))
  }

  /**
    * General request setter
    * @param url : Query URL
    * @param params : Query parmeter
    * @return : Future Result
    */
  def get(url: String, params: Set[(String, String)]): Future[WSResponse] = {
    Logger.info(s"Executing : \n ${params.toString}")
    client.url(config.get[String]("betaseries.baseURL") + url)
        .addQueryStringParameters("v" -> "3.0")
        .addQueryStringParameters(params.toList: _*)
        .addHttpHeaders("X-Betaseries-Key" -> config.get[String]("betaseries.APIKey"))
        .get()
  }

  def handleResponse(x: WSResponse) = {
    val message = s"${x.status.toString} - ${x.statusText} \n ${Json.prettyPrint(x.json)}"
    if (x.status == 200) {
      Logger.info(message)
      x.json
    } else {
      Logger.error(message)
      throw Exception(JsError(message))
    }
  }
}
