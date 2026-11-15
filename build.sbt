ThisBuild / scalaVersion := "2.13.18"

lazy val root = (project in file("."))
  .settings(
    name := "dar-vet",
    organization := "com.absolution",
    version := "0.0.1",
    libraryDependencies ++= Seq(
      "com.daml" %% "daml-lf-archive" % "3.7.3",
      "org.slf4j" % "slf4j-nop" % "2.0.16"
    ),
    run / fork := true,
    run / javaOptions += "-Xss16m",
    run / connectInput := false
  )
