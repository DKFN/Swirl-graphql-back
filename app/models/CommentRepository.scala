package models

import com.google.inject.Inject
import play.api.libs.json._
import play.api.Logger
import services.BetaSeries
import scala.concurrent.Await
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

class CommentRepository @Inject()(bsClient: BetaSeries) {

}
