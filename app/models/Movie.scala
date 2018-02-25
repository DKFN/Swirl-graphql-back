package models

case class Movie(
  id: Int,
  title: String,
  poster: String,
  backdrop: String,
  releaseDate: String,
  director: String,
  genres: Set[String],
  synopsis: String,
)