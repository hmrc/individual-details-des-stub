import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object MicroServiceBuild extends Build with MicroService {
  import play.sbt.routes.RoutesKeys._

  val appName = "individual-details-des-stub"

  override lazy val appDependencies: Seq[ModuleID] = compile ++ test()
  override lazy val playSettings : Seq[Setting[_]] = Seq(routesImport ++= Seq("uk.gov.hmrc.domain._", "uk.gov.hmrc.individualdetailsdesstub.domain._", "uk.gov.hmrc.individualdetailsdesstub.Binders._"))

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "microservice-bootstrap" % "6.18.0",
    "uk.gov.hmrc" %% "domain" % "4.1.0"
  )

  def test(scope: String = "test,it") = Seq(
    "uk.gov.hmrc" %% "hmrctest" % "2.3.0" % scope,
    "org.scalatest" %% "scalatest" % "2.2.6" % scope,
    "org.pegdown" % "pegdown" % "1.6.0" % scope,
    "org.mockito" % "mockito-all" % "1.10.19" % scope,
    "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
    "org.scalaj" %% "scalaj-http" % "2.3.0" % scope,
    "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % scope,
    "com.github.tomakehurst" % "wiremock" % "2.6.0" % scope
  )

}
