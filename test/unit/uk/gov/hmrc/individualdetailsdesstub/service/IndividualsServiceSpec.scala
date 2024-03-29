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

package unit.uk.gov.hmrc.individualdetailsdesstub.service

import java.time.LocalDate
import org.mockito.MockitoSugar
import uk.gov.hmrc.domain.{Nino, SaUtr, TaxIds}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.individualdetailsdesstub.connector.ApiPlatformTestUserConnector
import uk.gov.hmrc.individualdetailsdesstub.domain._
import uk.gov.hmrc.individualdetailsdesstub.service.IndividualsService
import unit.uk.gov.hmrc.individualdetailsdesstub.util.UnitSpec

import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext

class IndividualsServiceSpec extends UnitSpec with MockitoSugar {

  implicit val ec : ExecutionContext = ExecutionContext.global
  private val ninoNoSuffix = NinoNoSuffix("AB123456")
  private val nino = Nino("AB123456A")
  private val saUtr = SaUtr("12345")

  val testUser = TestIndividual(
    "945350439195",
    Some(saUtr),
    Some(nino),
    TestUserIndividualDetails("Adrian", "Adams", LocalDate.parse("1970-03-21"), TestUserAddress("1 Abbey Road", "Aberdeen")))

  val individual = Individual(
    ninoNoSuffix = ninoNoSuffix.nino,
    name = IndividualName(testUser.individualDetails.firstName, testUser.individualDetails.lastName),
    dateOfBirth = testUser.individualDetails.dateOfBirth,
    address = IndividualAddress(testUser.individualDetails.address.line1, testUser.individualDetails.address.line2)
  )

  val format = DateTimeFormatter.ofPattern("ddMMyyyy")

  val cidPerson = CidPerson(
    CidNames(CidName(testUser.individualDetails.firstName, testUser.individualDetails.lastName)),
    TaxIds(nino, saUtr),
    testUser.individualDetails.dateOfBirth.format(format))

  trait Setup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val mockTestUserConnector = mock[ApiPlatformTestUserConnector]
    val individualsService = new IndividualsService(mockTestUserConnector)

  }

  "Individuals service get by NINO function" should {

    "return a Cid Person for a matched NINO" in new Setup {
      when(mockTestUserConnector.getByNino(nino)(hc)).thenReturn(testUser)
      val result = await(individualsService.getCidPersonByNino(nino))
      result shouldBe cidPerson
    }

    "propagate a test user not found exception" in new Setup {
      when(mockTestUserConnector.getByNino(nino)(hc)).thenThrow(new TestUserNotFoundException)
      intercept[TestUserNotFoundException](await(individualsService.getCidPersonByNino(nino)))
    }
  }

  "Individuals service get by SHORTNINO function" should {

    "return an Individual for a matched SHORTNINO" in new Setup {
      when(mockTestUserConnector.getByShortNino(ninoNoSuffix)(hc)).thenReturn(testUser)
      val result = await(individualsService.getIndividualByShortNino(ninoNoSuffix))
      result shouldBe individual
    }

    "propagate a test user not found exception" in new Setup {
      when(mockTestUserConnector.getByShortNino(ninoNoSuffix)(hc)).thenThrow(new TestUserNotFoundException)
      intercept[TestUserNotFoundException](await(individualsService.getIndividualByShortNino(ninoNoSuffix)))
    }
  }

  "Individuals service get by SA UTR function" should {

    "return a Cid Person for a matched SA UTR" in new Setup {
      when(mockTestUserConnector.getBySaUtr(saUtr)(hc)).thenReturn(testUser)
      val result = await(individualsService.getCidPersonBySaUtr(saUtr))
      result shouldBe cidPerson
    }

    "propagate a test user not found exception" in new Setup {
      when(mockTestUserConnector.getBySaUtr(saUtr)(hc)).thenThrow(new TestUserNotFoundException)
      intercept[TestUserNotFoundException](await(individualsService.getCidPersonBySaUtr(saUtr)))
    }
  }

}
