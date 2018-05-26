package models

case class Strate (
  name: String,
  title: Option[String],
  movies: List[Movie],
)
