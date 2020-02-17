/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.individualdetailsdesstub.service

import javax.inject.{Inject, Singleton}

import uk.gov.hmrc.domain.{Nino, SaUtr}
import uk.gov.hmrc.individualdetailsdesstub.connector.ApiPlatformTestUserConnector
import uk.gov.hmrc.individualdetailsdesstub.domain._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

@Singleton
class IndividualsService @Inject()(apiPlatformTestUserConnector: ApiPlatformTestUserConnector) {

  def getIndividualByShortNino(shortNino: NinoNoSuffix)(implicit hc: HeaderCarrier): Future[Individual] =
    apiPlatformTestUserConnector.getByShortNino(shortNino) map (Individual(shortNino, _))

  def getCidPersonBySaUtr(saUtr: SaUtr)(implicit hc: HeaderCarrier): Future[CidPerson] =
    apiPlatformTestUserConnector.getBySaUtr(saUtr) map (CidPerson(saUtr, _))

  def getCidPersonByNino(nino: Nino)(implicit hc: HeaderCarrier): Future[CidPerson] = {
    apiPlatformTestUserConnector.getByNino(nino) map (CidPerson(nino, _))
  }

}
