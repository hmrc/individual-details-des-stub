import sbt._

object AppDependencies {

  val hmrcBootstrapVersion = "10.7.0"
  val playVersion = "play-30"

  val compile = Seq(
    "uk.gov.hmrc" %% s"bootstrap-backend-$playVersion" % hmrcBootstrapVersion,
    "uk.gov.hmrc" %% s"domain-$playVersion"            % "13.0.0"
  )

  val test = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-test-$playVersion" % hmrcBootstrapVersion % "test, it",
    "org.playframework" %% "play-ahc-ws-standalone"       % "3.0.7"              % "test, it"
  )
}
