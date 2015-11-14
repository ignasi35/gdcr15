
import sbt._

import sbt.Keys._

name := "akka-dddd-template"

version := "1.0.0"

organization := "boldradius"

scalaVersion := "2.11.6"

parallelExecution in Test := false

scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-target:jvm-1.7", "-deprecation", "-unchecked", "-Ywarn-dead-code", "-Xfatal-warnings", "-feature", "-language:postfixOps")
scalacOptions in (Compile, doc) <++= (name in (Compile, doc), version in (Compile, doc)) map DefaultOptions.scaladoc
javacOptions in (Compile, compile) ++= Seq("-source", "1.7", "-target", "1.7", "-Xlint:unchecked", "-Xlint:deprecation", "-Xlint:-options")

javaOptions += "-Xmx2G"


val akka = "2.3.9"


libraryDependencies ++= {
        Seq(
          "com.typesafe.akka"          %%  "akka-actor"                            % akka,
          "com.typesafe.akka"          %%  "akka-testkit"                          % akka     % "test",
          "org.scalatest"              %%  "scalatest"                             % "2.2.1"  % "test"
          )
      }





