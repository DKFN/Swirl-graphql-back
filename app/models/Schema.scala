package models
import sangria.schema._

object SchemaType {
  val Id = Argument("id", IntType)

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
      Field("synopsis", StringType, resolve = _.value.synopsis)
    )
  )

  val QueryType = ObjectType("Query", fields[MovieRepository, Unit](
    Field("movie", OptionType(MovieType),
      description = Some("Returns a movie with given Id"),
      arguments = Id :: Nil,
      resolve = c => c.ctx.Movie(c arg Id)
    )
  ))

  val schema = Schema(QueryType)
}

