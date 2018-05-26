package models

case class Movie(
  id: Int,
  title: String,
  poster: Option[String],
  backdrop: Option[String],
  releaseDate: String,
  director: Option[String],
  synopsis: String,
  trailerYoutubeId: Option[String],
  comments: List[Comment]
)
