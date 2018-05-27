name := "Swirl-GraphQL-Backend"
description := "A very simple backend server for GraphQL queries written with Play Framework, Sangria and some shy Akka"

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  guice,
  filters,
  ws,
  "org.sangria-graphql" %% "sangria" % "1.3.0",
  "org.sangria-graphql" %% "sangria-play-json" % "1.0.4"
)

routesGenerator := InjectedRoutesGenerator

