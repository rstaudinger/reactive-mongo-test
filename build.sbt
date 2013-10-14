name := "reactive-mongo"

version := "0.1"

scalaVersion := "2.10.0"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "reactivemongo" % "0.9",
  "org.scalatest" % "scalatest_2.10" % "2.0.RC1" % "test" ,
  "org.mockito" % "mockito-all" % "1.9.0" % "test"
)
