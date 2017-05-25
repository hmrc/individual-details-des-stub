/*
 * Copyright 2017 HM Revenue & Customs
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

package unit.uk.gov.hmrc.individualdetailsdesstub.controller

import org.joda.time.LocalDate
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import play.api.http.Status.OK
import play.api.inject.bind
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.domain.{Nino, TaxIds}
import uk.gov.hmrc.individualdetailsdesstub.domain._
import uk.gov.hmrc.individualdetailsdesstub.service.IndividualsService
import uk.gov.hmrc.individualdetailsdesstub.util.JsonFormatters._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future
import scala.concurrent.Future.{failed, successful}

class IndividualsControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  private val individualsService = mock[IndividualsService]
  override def bindModules: Seq[GuiceableModule] = Seq()

  override lazy val fakeApplication = new GuiceApplicationBuilder().configure("metrics.enabled" -> "false").configure("auditing.enabled" -> "false")
    .overrides(bind[IndividualsService].toInstance(individualsService))
    .build()

  implicit val materializer = fakeApplication.materializer

  val nino = Nino("AB123456A")
  val ninoNoSuffix = NinoNoSuffix("AB123456")

  val individual = Individual(
    ninoNoSuffix = ninoNoSuffix.nino,
    name = IndividualName("John", "Doe", Some("Peter")),
    dateOfBirth = LocalDate.parse("1980-01-10"),
    address = IndividualAddress("1 Stoke Ave", "West district", Some("Cardiff"), Some("Wales"), Some("SW11PT"), Some(1)))

  val cidPerson = CidPerson(CidNames(CidName("John", "Doe")), TaxIds(nino), "10011980")

  "fetchOrCreateIndividual" should {
    "return an openid individual and a http 200 (ok) when repository read is successful" in {
      mockIndividualsServiceReadToReturn(ninoNoSuffix, successful(Some(individual)))

      val result = invoke(GET, "/pay-as-you-earn/individuals/AB123456")

      status(result) shouldBe OK
      jsonBodyOf(result) shouldBe Json.parse(
        s"""
          |{
          |  "nino": "${ninoNoSuffix.value}",
          |  "names": {
          |    "1": {
          |      "firstForenameOrInitial": "${individual.name.firstForenameOrInitial}",
          |      "secondForenameOrInitial": "${individual.name.secondForenameOrInitial.get}",
          |      "surname": "${individual.name.surname}"
          |    }
          |  },
          |  "dateOfBirth": "${individual.dateOfBirth.toString("yyyy-MM-dd")}",
          |  "addresses": {
          |    "1": {
          |      "line1": "${individual.address.line1}",
          |      "line2": "${individual.address.line2}",
          |      "line3": "${individual.address.line3.get}",
          |      "line4": "${individual.address.line4.get}",
          |      "postcode": "${individual.address.postcode.get}",
          |      "countryCode": ${individual.address.countryCode.get}
          |    }
          |  }
          |}
        """.stripMargin)
    }

    "return an openid individual and a http 200 (ok) when repository read is unsuccessful and repository create is successful" in {
      mockIndividualsServiceReadToReturn(ninoNoSuffix, successful(None))
      mockIndividualsServiceCreateToReturn(ninoNoSuffix, successful(individual))

      val result = invoke(GET, "/pay-as-you-earn/individuals/AB123456")

      status(result) shouldBe OK
      jsonBodyOf(result) shouldBe Json.toJson(OpenidIndividual(individual))
    }

    "return a http 400 (Bad Request) when the nino is invalid" in {
      mockIndividualsServiceReadToReturn(ninoNoSuffix, successful(None))
      mockIndividualsServiceCreateToReturn(ninoNoSuffix, successful(individual))

      val result = invoke(GET, "/pay-as-you-earn/individuals/badnino")

      status(result) shouldBe BAD_REQUEST
    }

    "return a http 500 (internal server error) when repository read and create are unsuccessful" in {
      mockIndividualsServiceReadToReturn(ninoNoSuffix, successful(None))
      mockIndividualsServiceCreateToReturn(ninoNoSuffix, failed(new RuntimeException("simulated service exception")))

      val result = invoke(GET, "/pay-as-you-earn/individuals/AB123456")

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "getCidPerson" should {
    "return a sequence containing the cidPerson when the repository contains the individual" in {
      mockIndividualsServiceReadToReturn(nino, successful(Some(cidPerson)))

      val result = invoke(GET, s"/matching/find?nino=${nino.nino}")

      status(result) shouldBe OK
      jsonBodyOf(result) shouldBe Json.parse(
        s"""
          |[{
          |   "ids": {
          |     "nino": "${cidPerson.ids.nino.get}"
          |   },
          |   "name": {
          |     "current": {
          |       "firstName": "${cidPerson.name.current.firstName}",
          |       "lastName": "${cidPerson.name.current.lastName}"
          |     }
          |   },
          |   "dateOfBirth": "${cidPerson.dateOfBirth}"
          |}]
        """.stripMargin
      )
    }

    "return a 404 (Not Found) when the repository does not contain the individual" in {
      mockIndividualsServiceReadToReturn(nino, successful(None))

      val result = invoke(GET, s"/matching/find?nino=${nino.nino}")

      status(result) shouldBe NOT_FOUND
      jsonBodyOf(result) shouldBe Json.obj("code" -> "NOT_FOUND", "message" -> "Individual not found")
    }

    "return a 500 (Internal Server Error) when an error occurred" in {
      mockIndividualsServiceReadToReturn(nino, failed(new RuntimeException("test error")))

      val result = invoke(GET, s"/matching/find?nino=${nino.nino}")

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  private def invoke(httpVerb: String, uriPath: String): Result =
    await(route(fakeApplication, FakeRequest(GET, uriPath)).get)

  def mockIndividualsServiceReadToReturn(ninoNoSuffix: NinoNoSuffix, eventualMaybeIndividual: Future[Option[Individual]]) =
    when(individualsService.read(ninoNoSuffix)).thenReturn(eventualMaybeIndividual)

  def mockIndividualsServiceCreateToReturn(ninoNoSuffix: NinoNoSuffix, eventualIndividual: Future[Individual]) =
    when(individualsService.create(ninoNoSuffix)).thenReturn(eventualIndividual)

  def mockIndividualsServiceReadToReturn(nino: Nino, eventualMaybeCidPerson: Future[Option[CidPerson]]) =
    when(individualsService.getCidPerson(nino)).thenReturn(eventualMaybeCidPerson)

}