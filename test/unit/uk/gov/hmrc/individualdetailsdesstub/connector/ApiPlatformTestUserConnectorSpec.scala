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

package unit.uk.gov.hmrc.individualdetailsdesstub.connector

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import java.time.LocalDate
import org.scalatest.BeforeAndAfterEach
import play.api.test.Helpers._
import uk.gov.hmrc.domain.{Nino, SaUtr}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.individualdetailsdesstub.connector.ApiPlatformTestUserConnector
import uk.gov.hmrc.individualdetailsdesstub.domain._
import uk.gov.hmrc.individualdetailsdesstub.http.HttpClientOps
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import unit.uk.gov.hmrc.individualdetailsdesstub.util.utils.SpecBase

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

class ApiPlatformTestUserConnectorSpec extends SpecBase with BeforeAndAfterEach {

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  val stubPort = sys.env.getOrElse("WIREMOCK", "11121").toInt
  val stubHost = "localhost"
  val wireMockServer = new WireMockServer(wireMockConfig().port(stubPort))
  val nino = Nino("AB123456A")
  val testUser = TestIndividual(
    "945350439195",
    Some(SaUtr("12345")),
    Some(nino),
    TestUserIndividualDetails("Adrian", "Adams", LocalDate.parse("1970-03-21"), TestUserAddress("1 Abbey Road", "Aberdeen")))

  val testServicesConfig = fakeApplication.injector.instanceOf[ServicesConfig]
  val testHttpClient = fakeApplication.injector.instanceOf[HttpClientOps]

  trait Setup {
    implicit val hc = HeaderCarrier()

    val underTest = new ApiPlatformTestUserConnector(testServicesConfig, testHttpClient) {
      override val serviceUrl = "http://localhost:11121"
    }
  }

  override def beforeEach(): Unit = {
    wireMockServer.start()
    configureFor(stubHost, stubPort)
  }

  override def afterEach(): Unit = {
    wireMockServer.stop()
  }

  "get by nino" should {

    "retrieve a test user for a valid NINO" in new Setup {
      stubFor(get(urlEqualTo(s"/individuals/nino/${nino.nino}")).
        willReturn(aResponse().withStatus(OK).withBody(responsePayload)))

      await(underTest.getByNino(nino)) shouldBe testUser
    }

    "throw test user not found exception if test user cannot be found" in new Setup {
      stubFor(get(urlEqualTo(s"/individuals/nino/${nino.nino}")).
        willReturn(aResponse().withStatus(NOT_FOUND)))

      intercept[TestUserNotFoundException](await(underTest.getByNino(nino)))
    }
  }

  "get by short nino" should {

    val shortNino = NinoNoSuffix("AB123456")

    "retrieve a test user for a valid short NINO" in new Setup {
      stubFor(get(urlEqualTo(s"/individuals/shortnino/${shortNino.nino}")).
        willReturn(aResponse().withStatus(OK).withBody(responsePayload)))

      await(underTest.getByShortNino(shortNino)) shouldBe testUser
    }

    "throw test user not found exception if test user cannot be found" in new Setup {
      stubFor(get(urlEqualTo(s"/individuals/shortnino/${shortNino.nino}")).
        willReturn(aResponse().withStatus(NOT_FOUND)))

      intercept[TestUserNotFoundException](await(underTest.getByShortNino(shortNino)))
    }
  }

  "get by sa utr" should {

    val saUtr = SaUtr("1234567890")

    "retrieve a test user for a valid SA UTR" in new Setup {
      stubFor(get(urlEqualTo(s"/individuals/sautr/${saUtr.utr}")).
        willReturn(aResponse().withStatus(OK).withBody(responsePayload)))

      await(underTest.getBySaUtr(saUtr)) shouldBe testUser
    }

    "throw test user not found exception if test user cannot be found" in new Setup {
      stubFor(get(urlEqualTo(s"/individuals/sautr/${saUtr.utr}")).
        willReturn(aResponse().withStatus(NOT_FOUND)))

      intercept[TestUserNotFoundException](await(underTest.getBySaUtr(saUtr)))
    }
  }

  private val responsePayload =
    s"""{
         "userId": "945350439195",
         "password": "bLohysg8utsa",
         "saUtr": "12345",
         "nino": "AB123456A",
         "individualDetails": {
           "firstName": "Adrian",
           "lastName": "Adams",
           "dateOfBirth": "1970-03-21",
           "address": {
             "line1": "1 Abbey Road",
             "line2": "Aberdeen"
           }
         }
       }"""
}
