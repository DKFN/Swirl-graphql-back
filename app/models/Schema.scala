package models

import sangria.execution.deferred.Fetcher
import sangria.schema.{Field, ObjectType}
import services.BetaSeries

import scala.concurrent.Future

object Schema {
  /*val movies = Fetcher.caching(ctx: BetaSeries, ids: Seq[String]) =>
    Future.successful(ids.flatMap(id => ctx.get))*/
  /*
  val Movie = ObjectType(
    "Movie",
    "A movie that is a bit known",
  )*/
}
