package models

case class Strate (
  name: String,
  title: Option[String],
  movies: Seq[Movie],
)

case class StrateDef (
  name: String,
  title: Option[String],
  movieIds: Set[Int],
)
