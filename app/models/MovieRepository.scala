package models

import com.google.inject.Inject
import play.api.libs.json.JsValue
import services.BetaSeries

import scala.concurrent.ExecutionContext.Implicits.global

class MovieRepository @Inject()(bsClient: BetaSeries) {
  def Movie(id: Int) = {
    val gotten = bsClient.getMovie(id)
    gotten.map(z => {
      val x = (z \ "movie").as[JsValue]
      new Movie(
        id,
        (x \ "title").as[String],
        (x \ "poster").as[String],
        (x \ "backdrop").as[String],
        (x \ "release_date").as[String],
        (x \ "director").as[String],
        (x \ "synopsis").as[String]
      )
    })
  }

  def Movies(ids: List[Int]) = {
    ids.map(x => bsClient.getMovie(x))
  }
}
