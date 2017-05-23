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

package component.uk.gov.hmrc.itmpindividualdetailsstub

import org.scalatest.{BeforeAndAfterEach, Matchers, GivenWhenThen, FeatureSpec}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.itmpindividualdetailsstub.domain.{CidPerson, Individual, OpenidIndividual, NinoNoSuffix}
import uk.gov.hmrc.itmpindividualdetailsstub.repository.IndividualsRepository
import uk.gov.hmrc.itmpindividualdetailsstub.util.JsonFormatters._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration._
import scalaj.http.Http

class IndividualDetailsStubSpec extends FeatureSpec with Matchers with GivenWhenThen with GuiceOneServerPerSuite with BeforeAndAfterEach {
  override lazy val port = 19000
  override lazy val app = new GuiceApplicationBuilder()
    .configure("mongodb.uri" -> "mongodb://localhost:27017/itmp-individual-details-stub-it")
    .build()
  val timeout = 10.seconds
  val repository = app.injector.instanceOf[IndividualsRepository]

  val serviceUrl = s"http://localhost:$port"
  val nino = Nino("AB123456A")
  val ninoNoSuffix = NinoNoSuffix(nino)

  override def beforeEach = {
    Await.result(repository.drop, timeout)
    Await.result(repository.ensureIndexes, timeout)
  }
  override def afterEach = {
    Await.result(repository.drop, timeout)
  }

  feature("Creation and retrieval of user details for openid-connect") {

    scenario("Create individual on first fetch") {

      Given("The individual does not exist in the repository")

      When("I retrieve the individual by its NINO")
      val result = Http(s"$serviceUrl/pay-as-you-earn/individuals/${ninoNoSuffix.nino}").asString
      result.code shouldBe Status.OK

      Then("A generated individual is stored in mongo")
      val storedIndividual = Await.result(repository.read(ninoNoSuffix), timeout)
      storedIndividual shouldNot be (None)

      And("The individual is returned in an openid connect DES format")
      Json.parse(result.body) shouldBe Json.toJson(OpenidIndividual(storedIndividual.get))
    }

    scenario("Retrieve generated individual") {

      Given("The individual has already been fetched before")
      val firstIndividualFetchResponse = Http(s"$serviceUrl/pay-as-you-earn/individuals/${ninoNoSuffix.nino}").asString

      When("I retrieve the individual by its NINO")
      val secondIndividualFetchResponse = Http(s"$serviceUrl/pay-as-you-earn/individuals/${ninoNoSuffix.nino}").asString

      Then("The same individual is returned")
      Json.parse(firstIndividualFetchResponse.body) shouldBe Json.parse(secondIndividualFetchResponse.body)
    }

  }

  feature("Matching individual by NINO for citizen-details") {

    scenario("Look up a valid NINO") {

      Given("An individual in the database")
      val individual = createIndividualFor(ninoNoSuffix)

      When("I try to match the individual by its NINO")
      val result = Http(s"$serviceUrl/matching/find?nino=$nino").asString

      Then("The individual is returned")
      result.code shouldBe Status.OK
      Json.parse(result.body) shouldBe Json.toJson(CidPerson(nino, individual))
    }

    scenario("Look up an invalid NINO") {

      Given("The individual does not exist in the repository")

      When("I try to match the individual by its NINO")
      val result = Http(s"$serviceUrl/matching/find?nino=$nino").asString

      Then("A 404 (Not Found) is returned")
      result.code shouldBe Status.NOT_FOUND
      Json.parse(result.body) shouldBe Json.obj("code" -> "NOT_FOUND", "message" -> "Individual not found")
    }
  }

  private def createIndividualFor(ninoNoSuffix: NinoNoSuffix) = {
    val individual = Individual(ninoNoSuffix)
    Await.result(repository.create(individual), timeout)
    individual
  }
}
