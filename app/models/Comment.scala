package models

case class Comment(
  id: Int,
  date: String,
  login: String,
  avatar: String,
  text: String,
)