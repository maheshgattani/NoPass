name := """NoPass"""
organization := "com.example"

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += jdbc
libraryDependencies += "com.typesafe.play" %% "anorm" % "2.5.3"
libraryDependencies += evolutions
libraryDependencies += "com.h2database" % "h2" % "1.4.194"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.36"
