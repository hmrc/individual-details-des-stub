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

val appName = "individual-details-des-stub"

lazy val ItTest = config("it") extend Test
lazy val ComponentTest = config("component") extend Test

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    majorVersion := 0,
    scalaVersion := "3.7.1",
    onLoadMessage := "",
    scalafmtOnCompile := true,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:msg=Flag.*repeatedly:s",
    routesImport ++= Seq(
      "uk.gov.hmrc.domain._",
      "uk.gov.hmrc.individualdetailsdesstub.domain._",
      "uk.gov.hmrc.individualdetailsdesstub.Binders._"
    )
  )
  .settings(CodeCoverageSettings.settings*)
  .configs(ItTest)
  .settings(inConfig(ItTest)(Defaults.testSettings)*)
  .settings(
    ItTest / unmanagedSourceDirectories := Seq((ItTest / baseDirectory).value / "it")
  )
  .configs(ComponentTest)
  .settings(inConfig(ComponentTest)(Defaults.testSettings)*)
  .settings(
    ComponentTest / unmanagedSourceDirectories := Seq((ComponentTest / baseDirectory).value / "component")
  )
