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

package it.uk.gov.hmrc.individualdetailsdesstub.repository

import org.joda.time.LocalDate
import org.scalatest.BeforeAndAfterEach
import play.api.inject.guice.GuiceApplicationBuilder
import reactivemongo.api.commands.DefaultWriteResult
import uk.gov.hmrc.individualdetailsdesstub.domain.{NinoNoSuffix, Individual, IndividualAddress, IndividualName}
import uk.gov.hmrc.individualdetailsdesstub.repository.IndividualsRepository
import uk.gov.hmrc.mongo.MongoSpecSupport
import uk.gov.hmrc.play.test.{WithFakeApplication, UnitSpec}
import scala.concurrent.ExecutionContext.Implicits.global

class IndividualsRepositorySpec extends UnitSpec with MongoSpecSupport with WithFakeApplication with BeforeAndAfterEach {

  override lazy val fakeApplication = new GuiceApplicationBuilder()
    .configure("mongodb.uri" -> mongoUri)
    .bindings(bindModules:_*)
    .build()

  val individual = Individual("AB123456",
    IndividualName("John", "Doe"),
    LocalDate.parse("1980-01-10"),
    IndividualAddress("1 Stoke Ave", "Cardiff"))

  val individualRepository = fakeApplication.injector.instanceOf[IndividualsRepository]

  override def beforeEach() {
    await(individualRepository.drop)
    await(individualRepository.ensureIndexes)
  }

  override def afterEach() {
    await(individualRepository.drop)
  }

  "insert" should {
    "save an individual" in {
      await(individualRepository.create(individual))

      val storedIndividual = await(individualRepository.findById(NinoNoSuffix(individual.ninoNoSuffix)))
      storedIndividual shouldBe Some(individual)
    }

    "fail when trying to insert an individual with the same nino twice" in {
      val individualWithSameNino = Individual(individual.ninoNoSuffix,
        IndividualName("Mark", "Davis"),
        LocalDate.parse("1985-01-10"),
        IndividualAddress("10 Stoke Ave", "London"))

      await(individualRepository.create(individual))
      intercept[DefaultWriteResult]{await(individualRepository.create(individualWithSameNino))}
    }

  }

  "read" should {
    "return the individual when present in the database" in {
      await(individualRepository.create(individual))

      val result = await(individualRepository.read(NinoNoSuffix(individual.ninoNoSuffix)))

      result shouldBe Some(individual)
    }

    "return None when there is no individual for the nino" in {
      val result = await(individualRepository.read(NinoNoSuffix("AB123460")))

      result shouldBe None
    }

  }
}
