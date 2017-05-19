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

import javax.inject.{Inject, Singleton}

import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.itmpindividualdetailsstub.domain._
import uk.gov.hmrc.itmpindividualdetailsstub.repository.IndividualsRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class IndividualsService @Inject()(individualsRepository: IndividualsRepository) { // TODO add logging

  def create(shortNino: NinoNoSuffix): Future[Individual] =
    individualsRepository.create(Individual(shortNino))

  def read(shortNino: NinoNoSuffix): Future[Option[Individual]] =
    individualsRepository.read(shortNino)

  def getCidPerson(nino: Nino): Future[Option[CidPerson]] = {
    individualsRepository.read(NinoNoSuffix(nino)) map {_ map (CidPerson(nino, _))}
  }

}
