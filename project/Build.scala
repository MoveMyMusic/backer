import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "move-backend"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
   "com.typesafe.slick" %% "slick" % "1.0.0",
   "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
   "commons-codec" % "commons-codec" % "1.8"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
