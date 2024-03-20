/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings

val appName = "individual-details-des-stub"

lazy val ItTest = config("it") extend Test

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
  .settings(CodeCoverageSettings.settings: _*)
  .configs(ItTest)
  .settings(inConfig(ItTest)(Defaults.testSettings) *)
  .settings(resolvers += Resolver.jcenterRepo)
