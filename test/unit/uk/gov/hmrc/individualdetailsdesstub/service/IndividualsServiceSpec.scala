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

package unit.uk.gov.hmrc.individualdetailsdesstub.service

import org.joda.time.LocalDate
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.domain.{Nino, TaxIds}
import uk.gov.hmrc.individualdetailsdesstub.domain._
import uk.gov.hmrc.individualdetailsdesstub.repository.IndividualsRepository
import uk.gov.hmrc.individualdetailsdesstub.service.IndividualsService
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class IndividualsServiceSpec extends UnitSpec with MockitoSugar {

  private val individualsRepository = mock[IndividualsRepository]
  private val individualsService = new IndividualsService(individualsRepository)
  private val ninoNoSuffix = NinoNoSuffix("AB123456")
  private val nino = Nino("AB123456A")
  private val individual = Individual(
    ninoNoSuffix = ninoNoSuffix.nino,
    name = IndividualName("Amanda", "Jones"),
    dateOfBirth = LocalDate.parse("1970-01-04"),
    address = IndividualAddress("6 Oxford Street", "London")
  )
  private val cidPerson = CidPerson(CidNames(CidName("Amanda", "Jones")), TaxIds(nino), "04011970")

  "Individuals service create function" should {

    "delegate to repository create function" in {
      when(individualsRepository.create(any[Individual])).thenReturn(individual)
      await(individualsService.create(ninoNoSuffix))
      verify(individualsRepository).create(any[Individual])
    }

  }

  "Individuals service read function" should {

    "delegate to repository read function" in {
      when(individualsRepository.read(ninoNoSuffix)).thenReturn(None)
      await(individualsService.read(ninoNoSuffix))
      verify(individualsRepository).read(ninoNoSuffix)
    }

  }

  "getCidPerson" should {

    "fetch the individual and convert it into a CidPerson" in {
      when(individualsRepository.read(ninoNoSuffix)).thenReturn(Some(individual))

      val result = await(individualsService.getCidPerson(nino))

      result shouldBe Some(cidPerson)
    }

    "return None when there is no individual found for the nino" in {
      when(individualsRepository.read(ninoNoSuffix)).thenReturn(None)

      val result = await(individualsService.getCidPerson(nino))

      result shouldBe None
    }

    "fail when the repository fails" in {
      when(individualsRepository.read(ninoNoSuffix)).thenReturn(Future.failed(new RuntimeException("test error")))

      intercept[RuntimeException]{await(individualsService.getCidPerson(nino))}
    }

  }

}
