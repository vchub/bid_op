import sbt._
import Keys._
import play.Project._

object Dependencies {
  //val anorm = "anorm" %% "anorm" % "0.1"
  val h2_db_driver = "com.h2database" % "h2" % "1.2.127"
  val mysqlDriver = "mysql" % "mysql-connector-java" % "5.1.18"
  val postgresDriver = "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
  val squeryl_orm = "org.squeryl" %% "squeryl" % "0.9.5-6"
  val scalatest = "org.scalatest" %% "scalatest" % "1.9.1" % "test"
  //val codahale = "com.codahale" %% "jerkson" % "0.5.0"
  val fasterxml = "com.fasterxml.jackson.core" % "jackson-databind" % "2.0.0-RC3"
  //val lift_json = "net.liftweb" %% "lift-json" % "2.4"

  //TODO
  /**
   * Matrix library and so on --------------------------------------------------------
   * val breeze_math = "org.scalanlp" %% "breeze-math" % "0.1"
   * val breeze_learn = "org.scalanlp" %% "breeze-learn" % "0.1"
   * val breeze_process = "org.scalanlp" %% "breeze-process" % "0.1"
   * val breeze_viz = "org.scalanlp" %% "breeze-viz" % "0.1"
   * ---------------------------------------------------------------------------------
   */
}

object Resolvers {
  //val codahale = "codahale" at "http://repo.codahale.com"

  //TODO
  /**Matrix library and so on --------------------------------------------------------*/
  // other resolvers here
  // if you want to use snapshot builds (currently 0.2-SNAPSHOT), use this.
  // val breeze = "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

}

object ApplicationBuild extends Build {

  val appName = "bid"
  val appVersion = "1.0-SNAPSHOT"

  import Dependencies._
  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc, anorm, squeryl_orm, postgresDriver, fasterxml, mysqlDriver, scalatest) //, lift_json) //, codahale)

  //TODO
  //breeze_math, breeze_learn, breeze_process, breeze_viz)

  val main = play.Project(appName, appVersion, appDependencies).settings( //, mainLang = SCALA
    // Add your own project settings here
    testOptions in Test := Nil) /*,
    resolvers += Resolvers.codahale)*/

  //TODO
  //++= Seq(Resolvers.codahale,Resolvers.breeze))
}
