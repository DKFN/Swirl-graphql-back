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
  val Names = Argument("names", ListInputType(StringType))
  val Name = Argument("name", StringType)

  // TODO : Add a StrateType listing the fields and change the return
  // TODO : type of the strate main query to a ListType(StrateType)
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
      Field("poster", StringType, resolve = _.value.poster.orNull),
      Field("backdrop", StringType, resolve = _.value.backdrop.orNull),
      Field("releaseDate", StringType, resolve = _.value.releaseDate),
      Field("director", StringType, resolve = _.value.director.orNull),
      Field("synopsis", StringType, resolve = _.value.synopsis),
      Field("trailerYoutubeId", StringType, resolve = _.value.trailerYoutubeId.orNull),
      Field("comments", ListType(CommentType), resolve = _.value.comments)
    )
  )

  var StrateType = ObjectType(
    "Strate",
    "A strate used to display a list of movies",
    fields[Unit, Strate](
      Field("name", StringType, resolve = _.value.name),
      Field("title", StringType, resolve = _.value.title.getOrElse("Strate not found !")),
      Field("movies", ListType(MovieType), resolve = _.value.movies)
    )
  )

  val QueryType = ObjectType("Query", fields[MovieRepository, Unit](
    Field("movie", OptionType(MovieType),
      description = Some("Returns a movie with given Id"),
      arguments = Id :: Nil,
      resolve = c => c.ctx.Movie(c arg Id)
    ),
    Field("movies", ListType(MovieType),
      description = Some("Returns a list of movies with given Ids"),
      arguments = Ids :: Nil,
      resolve = c => c.ctx.Movies(c arg Ids)
    ),
    Field("strate", StrateType,
      description = Some("Returns lists of movies with given themes."),
      arguments = Name :: Nil,
      resolve = c => c.ctx.getStrate(c arg Name)
    ),
    Field("strates", ListType(StrateType),
      description = Some("Returns lists of movies with given themes."),
      arguments = Names :: Nil,
      resolve = c => c.ctx.getStrates(c arg Names)
    )
  ))

  val schema = Schema(QueryType)
}

