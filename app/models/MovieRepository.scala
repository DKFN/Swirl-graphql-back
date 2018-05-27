package models

import com.google.inject.Inject
import play.api.Logger
import play.api.libs.json.JsValue
import services.BetaSeries

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class MovieRepository @Inject()(bsClient: BetaSeries) {

  val stratesSet: Map[String, StrateDef] = Map[String, StrateDef](
    "anime" -> StrateDef("anime", Some("Films d'animation Japonais"), Set(3077, 5915, 5913, 232, 2908)),
    "soon" -> StrateDef("soon", Some("Prochaines Sorties"), Set(62541, 62291, 60911, 28205)),
    "drames" -> StrateDef("drame", Some("Films Dramatiques"), Set(3456, 4313, 142, 20104, 582, 2118)),
    "horreur" -> StrateDef("horreur", Some("Filrs d'horreur"), Set(5599, 3547, 4366, 491, 8254)),
    "fantastique" -> StrateDef("fantastique", Some("Films fantastiques"), Set(31798, 429, 8187, 4789)),
    "sciencefiction" -> StrateDef("sciencefiction", Some("Science Fiction"), Set(359, 3603, 662, 6, 308)),
    "thrillers" -> StrateDef("thrillers", Some("Thrillers"), Set(187, 3801, 3814, 3802)),
    "noirblanc" -> StrateDef("noirblanc", Some("Films classiques"), Set(260, 2687, 6905, 2059, 6712, 224))
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

  def getStrate(strate: String): Future[Strate] = {
    Logger.info(s"Asking strate : $strate")
    val maybeStrate = stratesSet.get(strate)
    Logger.info(s"Asking strate : ${maybeStrate.map(x => x.toString)}")
     maybeStrate match {
      case Some(x: StrateDef) => Movies(x.movieIds.toSeq).map(Strate(strate, x.title, _))
      case _ => Movies(Seq.empty).map(Strate(strate, None, _))
    }
  }

  def getStrates(strates: Seq[String]) = {
    Future.sequence(strates.map(getStrate))
  }
}
