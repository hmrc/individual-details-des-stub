/*
 * Copyright 2023 HM Revenue & Customs
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

package component.uk.gov.hmrc.individualdetailsdesstub

import component.uk.gov.hmrc.individualdetailsdesstub.stubs.{ApiPlatformTestUserStub, BaseSpec}
import org.apache.pekko.actor.ActorSystem

import java.time.LocalDate
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.domain.{Nino, SaUtr, TaxIds}
import uk.gov.hmrc.individualdetailsdesstub.domain.*
import uk.gov.hmrc.individualdetailsdesstub.util.JsonFormatters.*
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.{ExecutionContext, Future}

class IndividualDetailsStubSpec extends BaseSpec {

  implicit val actorSystem: ActorSystem = ActorSystem()
  val wsClient = StandaloneAhcWSClient()
  implicit val ec: ExecutionContext = ExecutionContext.global

  val nino = Nino("AB123456A")
  val ninoNoSuffix = NinoNoSuffix(nino)
  val saUtr = SaUtr("12345")

  Feature("Retrieval of user details for openid-connect") {

    Scenario("Retrieve an individual by SHORTNINO") {

      val individual = Individual(
        ninoNoSuffix = ninoNoSuffix.nino,
        name = IndividualName("Adrian", "Adams"),
        dateOfBirth = LocalDate.parse("1970-03-21"),
        address = IndividualAddress("1 Abbey Road", "Aberdeen")
      )

      Given("An individual exists for a given SHORTNINO")
      ApiPlatformTestUserStub.getByShortNinoReturnsTestUserDetails(nino, individual)

      When("I retrieve the individual by its SHORTNINO")
      val result = wsClient.url(s"$serviceUrl/pay-as-you-earn/individuals/${ninoNoSuffix.nino}").get()
      result.map { response =>
        response.status shouldBe Status.OK
        Then("The individual is returned in an openid connect DES format")
        Json.parse(response.body) shouldBe Json.toJson(OpenidIndividual(individual))
      }
    }

    Scenario("Retrieve an individual with a non-existing SHORTNINO") {

      Given("A test individual does not exist for a given SHORTNINO")
      ApiPlatformTestUserStub.getByShortNinoReturnsNoTestUser(ninoNoSuffix)

      When("I try to match the individual by its SHORTNINO")
      val result = wsClient.url(s"$serviceUrl/pay-as-you-earn/individuals/${ninoNoSuffix.nino}").get()

      Then("A 404 (Not Found) is returned")
      result.map { response =>
        response.status shouldBe Status.NOT_FOUND
        Json.parse(response.body) shouldBe Json.obj("code" -> "NOT_FOUND", "message" -> "Individual not found")
      }
    }
  }

  Feature("Retrieval of user details for openid-connect (2.0 endpoint)") {

    Scenario("Retrieve an individual by SHORTNINO") {

      val individual = Individual(
        ninoNoSuffix = ninoNoSuffix.nino,
        name = IndividualName("Adrian", "Adams"),
        dateOfBirth = LocalDate.parse("1970-03-21"),
        address = IndividualAddress("1 Abbey Road", "Aberdeen")
      )

      Given("An individual exists for a given SHORTNINO")
      ApiPlatformTestUserStub.getByShortNinoReturnsTestUserDetails(nino, individual)

      When("I retrieve the individual by its SHORTNINO")
      val result = wsClient.url(s"$serviceUrl/pay-as-you-earn/02.00.00/individuals/${ninoNoSuffix.nino}").get()
      result.map { response =>
        response.status shouldBe Status.OK

        Then("The individual is returned in an openid connect DES format")
        Json.parse(response.body) shouldBe Json.toJson(OpenidIndividual(individual))
      }
    }

    Scenario("Retrieve an individual with a non-existing SHORTNINO") {

      Given("A test individual does not exist for a given SHORTNINO")
      ApiPlatformTestUserStub.getByShortNinoReturnsNoTestUser(ninoNoSuffix)

      When("I try to match the individual by its SHORTNINO")
      val result = wsClient.url(s"$serviceUrl/pay-as-you-earn/02.00.00/individuals/${ninoNoSuffix.nino}").get()

      Then("A 404 (Not Found) is returned")
      result.map { response =>
        response.status shouldBe Status.NOT_FOUND
        Json.parse(response.body) shouldBe Json.obj("code" -> "NOT_FOUND", "message" -> "Individual not found")
      }
    }
  }

  Feature("Retrieval of user details for citizen-details matching") {

    Scenario("Retrieve an individual by NINO") {

      val cidPerson = CidPerson(CidNames(CidName("Adrian", "Adams")), TaxIds(nino, saUtr), "21031970")

      Given("A test individual exists for a given NINO")
      ApiPlatformTestUserStub.getByNinoReturnsTestUserDetails(nino, cidPerson)

      When("I retrieve the individual by its NINO")
      val result = wsClient.url(s"$serviceUrl/matching/find?nino=${nino.nino}").get()
      result.map { response =>
        response.status shouldBe Status.OK

        Then("The individual is returned in an citizen-details format")
        Json.parse(response.body) shouldBe Json.toJson(Seq(cidPerson))
      }
    }

    Scenario("Retrieve an individual with a non existing NINO") {

      Given("A test individual does not exist for a given NINO")
      ApiPlatformTestUserStub.getByNinoReturnsNoTestUser(nino)

      When("I try to match the individual by its NINO")
      val result = wsClient.url(s"$serviceUrl/matching/find?nino=${nino.nino}").get()

      Then("A 404 (Not Found) is returned")
      result.map { response =>
        response.status shouldBe Status.NOT_FOUND
        Json.parse(response.body) shouldBe Json.obj("code" -> "NOT_FOUND", "message" -> "Individual not found")
      }
    }

    Scenario("Retrieve an individual by SA UTR") {

      val cidPerson = CidPerson(CidNames(CidName("Adrian", "Adams")), TaxIds(nino, saUtr), "21031970")

      Given("A test individual exists for a given SA UTR")
      ApiPlatformTestUserStub.getBySaUtrReturnsTestUserDetails(saUtr, cidPerson)

      When("I retrieve the individual by its SA UTR")
      val result = wsClient.url(s"$serviceUrl/matching/find?sautr=${saUtr.utr}").get()
      result.map { response =>
        response.status shouldBe Status.OK

        Then("The individual is returned in an citizen-details format")
        Json.parse(response.body) shouldBe Json.toJson(Seq(cidPerson))
      }
    }

    Scenario("Retrieve an individual with a non existing SA UTR") {

      Given("A test individual does not exist for a given SA UTR")
      ApiPlatformTestUserStub.getBySaUtrReturnsNoTestUser(saUtr)

      When("I try to match the individual by its SA UTR")
      val result = wsClient.url(s"$serviceUrl/matching/find?sautr=${saUtr.utr}").get()

      Then("A 404 (Not Found) is returned")
      result.map { response =>
        response.status shouldBe Status.NOT_FOUND
        Json.parse(response.body) shouldBe Json.obj("code" -> "NOT_FOUND", "message" -> "Individual not found")
      }
    }

    Scenario("Retrieve an individual without a NINO or SA UTR") {

      Given("A no NINO or SA UTR parameters")
      val result = wsClient.url(s"$serviceUrl/matching/find").get()

      When("I invoke the '/matching/find' endpoint")

      Then("A 400 (Bad Request) is returned")
      result.map { response =>
        response.status shouldBe Status.BAD_REQUEST
        Json.parse(response.body) shouldBe Json.obj("code" -> "BAD_REQUEST", "message" -> "sautr or nino is required")
      }
    }
  }
}
