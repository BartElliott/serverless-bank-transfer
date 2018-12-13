import sbt.Keys._
import sbt._
import sbtrelease.Version

name := "serverless-transfer"

resolvers += Resolver.sonatypeRepo("public")
scalaVersion := "2.12.6"
releaseNextVersion := { ver => Version(ver).map(_.bumpMinor.string).getOrElse("Error") }
assemblyJarName in assembly := "transfer.jar"

libraryDependencies ++= Seq(
  "com.amazonaws"                 % "aws-lambda-java-events"    % "2.2.1",
  "com.amazonaws"                 % "aws-lambda-java-core"      % "1.2.0",
  "com.amazonaws"                 % "aws-java-sdk-sqs"          % "1.11.466",
  "com.gu"                        %% "scanamo"                  % "1.0.0-M8",
  "com.fasterxml.jackson.module"  %% "jackson-module-scala"     % "2.9.7",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-joda"    % "2.9.7"
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings")
