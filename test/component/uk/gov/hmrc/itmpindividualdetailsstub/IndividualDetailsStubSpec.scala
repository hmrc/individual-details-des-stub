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

import org.scalatest.{Matchers, GivenWhenThen, FeatureSpec}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.itmpindividualdetailsstub.domain.{Individual, NinoNoSuffix}
import uk.gov.hmrc.itmpindividualdetailsstub.repository.IndividualsRepository
import uk.gov.hmrc.itmpindividualdetailsstub.util.JsonFormatters._

import scala.concurrent.Await
import scala.concurrent.duration._
import scalaj.http.Http

class IndividualDetailsStubSpec extends FeatureSpec with Matchers with GivenWhenThen with GuiceOneServerPerSuite {

  override lazy val port = 19000
  val serviceUrl = s"http://localhost:$port"
  val nino = Nino("AB123456A")
  val ninoNoSuffix = NinoNoSuffix(nino)

  feature("Creation and retrieval of user details for openid-connect") {

    scenario("Create individual on first fetch") {

      Given("The individual does not exist in the repository")

      When("I retrieve the individual by its NINO")
      val result = Http(s"$serviceUrl/pay-as-you-earn/individuals/${ninoNoSuffix.nino}").asString
      result.code shouldBe Status.OK

      Then("A generated individual is returned")
      val individual = Json.parse(result.body).as[Individual]

      And("The individual is stored in mongo")
      val storedIndividual = Await.result(app.injector.instanceOf[IndividualsRepository].read(ninoNoSuffix), 10.seconds)
      storedIndividual shouldBe Some(individual)
    }

    scenario("Retrieve generated individual") {

      Given("The individual has already been fetched before")
      val response = Http(s"$serviceUrl/pay-as-you-earn/individuals/${ninoNoSuffix.nino}").asString
      val individualOnFirstFetch = Json.parse(response.body).as[Individual]

      When("I retrieve the individual by its NINO")
      val result = Http(s"$serviceUrl/pay-as-you-earn/individuals/${ninoNoSuffix.nino}").asString
      val individualOnSecondFetch = Json.parse(response.body).as[Individual]

      Then("The same individual is returned")
      individualOnSecondFetch shouldBe individualOnFirstFetch
    }

  }
}
