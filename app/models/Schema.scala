package models
import sangria.schema._
import sangria.execution._
import sangria.execution.deferred.{Fetcher, HasId}
import play.api.libs.json.JsValue

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

object SchemaType {
  val Id = Argument("id", IntType)
  val Ids = Argument("id", ListInputType(IntType))

  val CommentType = ObjectType(
    "Comment",
    "An user comment about a movie",
    fields[Unit, Comment](
      Field("id", IntType, resolve = _.value.id),
      Field("date", StringType, resolve = _.value.date),
      Field("login", StringType, resolve = _.value.login),
      Field("avatar", StringType, resolve = _.value.avatar),
      Field("text", StringType, resolve = _.value.text)
    )
  )

  val MovieType = ObjectType(
    "Movie",
    "A movie that is a bit known",
    fields[Unit, Movie](
      Field("id", IntType, resolve = _.value.id),
      Field("title", StringType, resolve = _.value.title),
      Field("poster", StringType, resolve = _.value.poster),
      Field("backdrop", StringType, resolve = _.value.backdrop),
      Field("releaseDate", StringType, resolve = _.value.releaseDate),
      Field("director", StringType, resolve = _.value.director),
      Field("synopsis", StringType, resolve = _.value.synopsis),
      Field("comments", ListType(CommentType), resolve = _.value.comments)
    )
  )

  val movies = Fetcher.caching(
    (ctx: MovieRepository, ids: Seq[Int]) =>
      Future.successful(ctx.Movies(ids)).map(x => x)
  )(HasId((x) => 1))

  val QueryType = ObjectType("Query", fields[MovieRepository, Unit](
    Field("movie", OptionType(MovieType),
      description = Some("Returns a movie with given Id"),
      arguments = Id :: Nil,
      resolve = c => c.ctx.Movie(c arg Id)
    )/*,
    Field("movies", ListType(MovieType),
      description = Some("Returns a list of movies with given Ids"),
      arguments = Ids :: Nil,
      resolve = c => movies.deferSeqOpt(c.ctx.Movies(c arg Ids))
    )*/
  ))

  val schema = Schema(QueryType)
}

