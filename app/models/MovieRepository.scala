package models

import com.google.inject.Inject
import play.api.libs.json.JsValue
import play.api.Logger
import services.BetaSeries

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class MovieRepository @Inject()(bsClient: BetaSeries) {
  def Movie(id: Int) = {
    val pendingMovies = bsClient.getMovie(id)
    val pendingComments = getComments(id)

    for {
      movies <- pendingMovies
      comments <- pendingComments
    } yield {
      val x = (movies \ "movie").as[JsValue]
      new Movie(
        id,
        (x \ "title").as[String],
        (x \ "poster").as[String],
        (x \ "backdrop").as[String],
        (x \ "release_date").as[String],
        (x \ "director").as[String],
        (x \ "synopsis").as[String],
        comments
      )
    }
  }

  def getComments(id: Int): Future[List[Comment]] = {
    val fetchingComments = bsClient.getComments(id);
    fetchingComments.map(z => {
      (z \ "comments").as[List[JsValue]].map(x => {
        Comment(
          (x \ "id").as[Int],
          (x \ "date").asOpt[String].getOrElse(""),
          (x \ "login").asOpt[String].getOrElse(""),
          (x \ "avatar").asOpt[String].getOrElse(""),
          (x \ "text").asOpt[String].getOrElse("")
        )
      })
    })
  }

  def Movies(ids: Seq[Int]) = {
    ids.map(x => bsClient.getMovie(x))
  }
}
