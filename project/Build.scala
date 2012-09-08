import sbt._
import Keys._
import PlayProject._

object Dependencies {
  val anorm = "anorm" %% "anorm" % "0.1"
  val h2_db_driver = "com.h2database" % "h2" % "1.2.127"
  val mysqlDriver = "mysql" % "mysql-connector-java" % "5.1.10"
  val postgresDriver = "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
  val squeryl_orm = "org.squeryl" %% "squeryl" % "0.9.5-2"
  val scalatest = "org.scalatest" %% "scalatest" % "1.8" % "test"
}

object ApplicationBuild extends Build {

    val appName         = "bid"
    val appVersion      = "1.0-SNAPSHOT"

    import Dependencies._
    val appDependencies = Seq(
      // Add your project dependencies here,
      squeryl_orm, postgresDriver, scalatest
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here
      testOptions in Test := Nil
    )

}
