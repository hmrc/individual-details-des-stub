import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings

val appName = "individual-details-des-stub"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    majorVersion := 0,
    scalaVersion := "2.13.12",
    onLoadMessage := "",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions += "-Wconf:src=routes/.*:s",
    routesImport ++= Seq("uk.gov.hmrc.domain._", "uk.gov.hmrc.individualdetailsdesstub.domain._", "uk.gov.hmrc.individualdetailsdesstub.Binders._"),
    // Minimal test logging config
    testOptions -= Tests.Argument("-o", "-u", "target/int-test-reports", "-h", "target/int-test-reports/html-report"),
    testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oNCHPQR", "-u", "target/int-test-reports", "-h", "target/int-test-reports/html-report")
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
