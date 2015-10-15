import com.github.retronym.SbtOneJar._

name := "nestor_bot"

version := "1.0"

scalaVersion := "2.11.7"

oneJarSettings

libraryDependencies +=
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"