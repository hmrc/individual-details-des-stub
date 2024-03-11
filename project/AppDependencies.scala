import sbt._

object AppDependencies {

  val hmrcBootstrapVersion = "8.5.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % hmrcBootstrapVersion,
    "uk.gov.hmrc" %% "domain" % "8.0.0-play-28",
    "com.typesafe.play" %% "play-json-joda" % "2.9.1"
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % hmrcBootstrapVersion % "test, it",
    "org.scalaj" %% "scalaj-http" % "2.4.2" % "test, it",
    "com.github.tomakehurst" % "wiremock-jre8" % "2.27.2" % "test, it",
    "org.mockito" % "mockito-scala_2.13" % "1.17.12" % "test, it",
  )
}
