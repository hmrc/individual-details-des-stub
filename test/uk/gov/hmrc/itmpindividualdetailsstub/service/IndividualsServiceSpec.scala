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

package uk.gov.hmrc.itmpindividualdetailsstub.service

import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.itmpindividualdetailsstub.domain.{Individual, ShortNino}
import uk.gov.hmrc.itmpindividualdetailsstub.repository.IndividualsRepository
import uk.gov.hmrc.play.test.UnitSpec

class IndividualsServiceSpec extends UnitSpec with MockitoSugar {

  private val individualsRepository = mock[IndividualsRepository]
  private val individualsService = new IndividualsService(individualsRepository)
  private val shortNino = ShortNino("AB123456")
  private val individual = Individual(shortNino)

  "Individuals service create function" should {

    "delegate to repository create function" in {
      when(individualsRepository.create(any[Individual])).thenReturn(individual)
      await(individualsService.create(shortNino))
      verify(individualsRepository).create(any[Individual])
    }

  }

  "Individuals service read function" should {

    "delegate to repository read function" in {
      when(individualsRepository.read(shortNino)).thenReturn(None)
      await(individualsService.read(shortNino))
      verify(individualsRepository).read(shortNino)
    }

  }

}
