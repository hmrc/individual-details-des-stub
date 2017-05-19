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

package uk.gov.hmrc.itmpindividualdetailsstub.repository

import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import reactivemongo.api.ReadPreference
import uk.gov.hmrc.itmpindividualdetailsstub.domain.{NinoNoSuffix, Individual}
import uk.gov.hmrc.itmpindividualdetailsstub.util.JsonFormatters
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IndividualsRepository @Inject()(mongoConnectionProvider: MongoConnectionProvider)
  extends ReactiveRepository[Individual, NinoNoSuffix]("individual", mongoConnectionProvider.mongoDatabase, JsonFormatters.individualJsonFormat, JsonFormatters.shortNinoJsonFormat) {

  def create(individual: Individual): Future[Individual] = {
    insert(individual) map { writeResult =>
      if (writeResult.n == 1) individual
      else throw new RuntimeException(s"failed to persist individual $individual")
    }
  }

  def read(ninoNoSuffix: NinoNoSuffix): Future[Option[Individual]] = findById(ninoNoSuffix)

  override def findById(id: NinoNoSuffix, readPreference: ReadPreference)(implicit ec: ExecutionContext): Future[Option[Individual]] = collection.find(Json.obj("id" -> id.nino)).one[Individual]

}