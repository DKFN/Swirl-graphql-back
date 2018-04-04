package models

case class Movie(
  id: Int,
  title: String,
  poster: String,
  backdrop: String,
  releaseDate: String,
  director: String,
  synopsis: String,
  comments: List[Comment]
)
