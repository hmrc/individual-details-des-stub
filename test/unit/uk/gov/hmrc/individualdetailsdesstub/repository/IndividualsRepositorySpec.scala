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

package unit.uk.gov.hmrc.individualdetailsdesstub.repository

import org.joda.time.LocalDate
import org.mockito.BDDMockito.given
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, doReturn, spy, when}
import org.scalatest.Matchers
import org.scalatest.mockito.MockitoSugar
import reactivemongo.api.commands.{DefaultWriteResult, WriteResult}
import reactivemongo.api.indexes.IndexType.Ascending
import reactivemongo.api.indexes.{Index, CollectionIndexesManager}
import reactivemongo.json.collection.JSONCollection
import uk.gov.hmrc.individualdetailsdesstub.domain.{Individual, IndividualAddress, IndividualName}
import uk.gov.hmrc.individualdetailsdesstub.repository.{IndividualsRepository, MongoConnectionProvider}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

class IndividualsRepositorySpec extends UnitSpec with MockitoSugar with Matchers {

  private val jsonCollection = mock[JSONCollection]
  private val indexManager = mock[CollectionIndexesManager]

  trait Setup {
    val success = DefaultWriteResult(ok = true, 1, Seq(), None, None, None)

    given(jsonCollection.indexesManager(any())).willReturn(indexManager)
    given(indexManager.create(any())).willReturn(success)

    val individualsRepository = spy(new TestNinoMatchRepository(mock[MongoConnectionProvider]))
  }

  "Individuals repository" should {
    "create index for nino" in new Setup {
      verify(indexManager).create(Index(Seq(("ninoNoSuffix", Ascending)), Some("ninoNoSuffixIndex"), background = true, unique = true))
    }
  }

  "Individuals repository create function" should {

    val individual = Individual("AB123456",
      IndividualName("John", "Doe"),
      LocalDate.parse("1980-01-10"),
      IndividualAddress("1 Stoke Ave", "Cardiff"))

    def mockRepositoryInsertToReturn(individualsRepository: IndividualsRepository, writeResult: WriteResult, resultCount: Int) = {
      doReturn(successful(writeResult)).when(individualsRepository).insert(any[Individual])(any[ExecutionContext])
      when(writeResult.n).thenReturn(resultCount)
    }

    "return an individual when creation is successful" in new Setup {
      mockRepositoryInsertToReturn(individualsRepository, mock[WriteResult], 1)
      await(individualsRepository.create(individual)) shouldBe individual
    }

    "propagate an exception when creation is unsuccessful" in new Setup {
      mockRepositoryInsertToReturn(individualsRepository, mock[WriteResult], 0)
      intercept[RuntimeException] {
        await(individualsRepository.create(individual))
      }.getMessage.startsWith("failed to persist individual") shouldBe true
    }

  }

  class TestNinoMatchRepository(mongoConnectionProvider: MongoConnectionProvider) extends IndividualsRepository(mongoConnectionProvider) {
    override lazy val collection = jsonCollection
  }
}
