import sbt._

object AppDependencies {

  val hmrcBootstrapVersion = "8.5.0"
  val playVersion = "play-30"


  val compile = Seq(
    "uk.gov.hmrc" %% s"bootstrap-backend-$playVersion" % hmrcBootstrapVersion,
    "uk.gov.hmrc" %% s"domain-$playVersion" % s"9.0.0",
  )

  val test = Seq(
    "uk.gov.hmrc" %% s"bootstrap-test-$playVersion" % hmrcBootstrapVersion % "test, it",
    "org.scalaj" %% "scalaj-http" % "2.4.2" % "test, it",
    "org.mockito" % "mockito-scala_2.13" % "1.17.30" % "test, it"
  )
}
