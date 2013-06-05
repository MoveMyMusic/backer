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
   "commons-codec" % "commons-codec" % "1.8",
   "org.scalaz" %% "scalaz-core" % "7.0.0",
   "org.typelevel" %% "scalaz-contrib-validation" % "0.1.4",
   "com.chuusai" %% "shapeless" % "1.2.4"

  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
