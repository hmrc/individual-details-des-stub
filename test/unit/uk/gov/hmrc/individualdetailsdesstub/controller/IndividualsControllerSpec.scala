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
import org.mockito.Matchers.{any, refEq}
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status.OK
import play.api.inject.bind
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.domain.{Nino, SaUtr, TaxIds}
import uk.gov.hmrc.individualdetailsdesstub.domain._
import uk.gov.hmrc.individualdetailsdesstub.service.IndividualsService
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future._

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

  implicit val hc = HeaderCarrier()

  "find Individual by SHORTNINO" should {
    "return an openid individual and a http 200 (ok) when a test user exists for a given SHORTNINO" in {
      when(individualsService.getIndividualByShortNino(refEq(ninoNoSuffix))(any[HeaderCarrier])).thenReturn(successful(individual))

      val result = invoke(GET, s"/pay-as-you-earn/individuals/${ninoNoSuffix.nino}")

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

    "return a http 400 (Bad Request) when the SHORTNINO is invalid" in {
      val result = invoke(GET, "/pay-as-you-earn/individuals/badnino")
      status(result) shouldBe BAD_REQUEST
    }

    "return a 500 (Internal Server Error) when an error occurred" in {
      when(individualsService.getIndividualByShortNino(refEq(ninoNoSuffix))(any[HeaderCarrier])).thenReturn(failed(new RuntimeException("test error")))

      val result = invoke(GET, s"/pay-as-you-earn/individuals/${ninoNoSuffix.nino}")

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "find Individual by SA UTR" should {

    val saUtr = SaUtr("1234567890")

    "return an openid individual and a http 200 (ok) when a test user exists for a given SA UTR" in {
      when(individualsService.getCidPersonBySaUtr(refEq(saUtr))(any[HeaderCarrier])).thenReturn(successful(cidPerson))

      val result = invoke(GET, s"/matching/find?saUtr=${saUtr.utr}")

      status(result) shouldBe OK
      jsonBodyOf(result) shouldBe Json.parse(
        s"""
           [{
              "ids": {
                "nino": "${cidPerson.ids.nino.get}"
              },
              "name": {
                "current": {
                  "firstName": "${cidPerson.name.current.firstName}",
                  "lastName": "${cidPerson.name.current.lastName}"
                }
              },
              "dateOfBirth": "${cidPerson.dateOfBirth}"
           }]
        """)
    }

    "return a 500 (Internal Server Error) when an error occurred" in {
      when(individualsService.getCidPersonBySaUtr(refEq(saUtr))(any[HeaderCarrier])).thenReturn(failed(new RuntimeException("test error")))

      val result = invoke(GET, s"/matching/find?saUtr=${saUtr.utr}")

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "getCidPerson by NINO" should {
    "return a sequence containing the cidPerson a test user exists for a given NINO" in {
      when(individualsService.getCidPersonByNino(refEq(nino))(any[HeaderCarrier])).thenReturn(successful(cidPerson))

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

    "return a http 400 (Bad Request) when the NINO is invalid" in {
      val result = invoke(GET, "/matching/find?nino=badnino")
      status(result) shouldBe BAD_REQUEST
    }

    "return a 500 (Internal Server Error) when an error occurred" in {
      when(individualsService.getCidPersonByNino(refEq(nino))(any[HeaderCarrier])).thenReturn(failed(new RuntimeException("test error")))

      val result = invoke(GET, s"/matching/find?nino=${nino.nino}")

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  private def invoke(httpVerb: String, uriPath: String): Result =
    await(route(fakeApplication, FakeRequest(GET, uriPath)).get)
}
