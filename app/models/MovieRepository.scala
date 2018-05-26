package models

import com.google.inject.Inject
import play.api.Logger
import play.api.libs.json.JsValue
import services.BetaSeries

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class MovieRepository @Inject()(bsClient: BetaSeries) {

  val stratesSet: Map[String, Set[Int]] = Map[String, Set[Int]](
    "anime" -> Set(60678, 31435, 10745),
    "soon" -> Set(62541, 62291, 60911, 28205)
  )

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
        (x \ "poster").asOpt[String],
        (x \ "backdrop").asOpt[String],
        (x \ "release_date").as[String],
        (x \ "director").asOpt[String],
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

  def getStrate(strate: String): Future[Seq[Movie]] = {
    Logger.info(s"Asking strate : $strate")

    val maybeStrate = stratesSet.get(strate)

    Logger.info(s"Asking strate : ${maybeStrate.map(x => x.toString)}")

    val gotten = maybeStrate match {
      case Some(x: Set[Int]) => Movies(x.toSeq)
      case _ => Movies(Seq.empty)
    }
    Logger.info(gotten.toString)
    gotten
  }

  def getStrates(strates: Seq[String]) = {
    Future.sequence(strates.map(getStrate))
  }
}
