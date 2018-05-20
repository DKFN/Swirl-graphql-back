package models

import com.google.inject.Inject
import play.api.Logger
import play.api.libs.json.JsValue
import services.BetaSeries

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class MovieRepository @Inject()(bsClient: BetaSeries) {

  val stratesSet: Map[String, Set[Int]] = Map[String, Set[Int]]()

  def initStratesSet(): Unit = {
    stratesSet ++ List(
      "animes" -> Set(135, 2, 3)
    )
  }

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
        (x \ "trailer").asOpt[String],
        comments
      )
    }
  }

  def getComments(id: Int): Future[List[Comment]] = {
    val fetchingComments = bsClient.getComments(id)
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

  def Movies(ids: Seq[Int]) = Future.sequence(ids.map(Movie))

  // TODO : Homepage Movies must display a selection of movie depeding on the passed strates
  // TODO : Defaults to homepage
  def homepageMovies(strates: Seq[String]) = {
    initStratesSet()
    Logger.debug(strates.map(x => stratesSet.get(x)).toString)
    Movies(List(132, 1 , 2))
  }
}
