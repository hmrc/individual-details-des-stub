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

package uk.gov.hmrc.itmpindividualdetailsstub.controller

import java.util.concurrent.TimeUnit.SECONDS

import org.joda.time.LocalDate
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterAll
import org.scalatest.mock.MockitoSugar
import play.api.Play.{start, stop}
import play.api.http.Status.OK
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, route, _}
import uk.gov.hmrc.itmpindividualdetailsstub.domain.{IndividualAddress, IndividualName, Individual, ShortNino}
import uk.gov.hmrc.itmpindividualdetailsstub.service.IndividualsService
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future
import scala.concurrent.Future.{failed, successful}
import scala.concurrent.duration.FiniteDuration

class IndividualsControllerSpec extends UnitSpec with BeforeAndAfterAll with MockitoSugar {

  private val individualsService = mock[IndividualsService]

  private val application = new GuiceApplicationBuilder()
    .overrides(bind[IndividualsService].toInstance(individualsService))
    .build()

  implicit val materializer = application.materializer

  implicit val finiteDuration = FiniteDuration(10, SECONDS)

  val individual = Individual("AB123456",
    IndividualName("John", "Doe"),
    LocalDate.parse("1980-01-10"),
    IndividualAddress("1 Stoke Ave", "Cardiff"))

  "Individuals controller get function" should {

    val shortNino = ShortNino("AB123456")

    def mockIndividualsServiceReadToReturn(eventualMaybeIndividual: Future[Option[Individual]]) =
      when(individualsService.read(shortNino)).thenReturn(eventualMaybeIndividual)

    def mockIndividualsServiceCreateToReturn(eventualIndividual: Future[Individual]) =
      when(individualsService.create(shortNino)).thenReturn(eventualIndividual)

    def invoke(httpVerb: String, uriPath: String): Result =
      await(route(application, FakeRequest(GET, uriPath)).get)

    "return an individual and a http 200 (ok) when repository read is successful" in {
      mockIndividualsServiceReadToReturn(successful(Some(individual)))
      val result = invoke(GET, "/pay-as-you-earn/individuals/AB123456")
      status(result) shouldBe OK
      bodyOf(result) shouldBe "moo"
    }

    "return an individual and a http 200 (ok) when repository read is unsuccessful and repository create is successful" ignore {
      mockIndividualsServiceReadToReturn(successful(None))
      mockIndividualsServiceCreateToReturn(successful(Individual(shortNino)))
      val result = invoke(GET, "/pay-as-you-earn/individuals/AB123456")
      status(result) shouldBe OK
      bodyOf(result) shouldBe "moo"
    }

    "return a http 500 (internal server error) when repository read and create are unsuccessful" ignore {
      mockIndividualsServiceReadToReturn(successful(None))
      mockIndividualsServiceCreateToReturn(failed(new RuntimeException("simulated service exception")))
      val result = invoke(GET, "/pay-as-you-earn/individuals/AB123456")
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }

  }

  override protected def beforeAll() = start(application)

  override protected def afterAll() = stop(application)

}
