/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.individualdetailsdesstub.connector

import javax.inject.Singleton

import uk.gov.hmrc.domain.{Nino, SaUtr}
import uk.gov.hmrc.individualdetailsdesstub.config.WSHttp
import uk.gov.hmrc.individualdetailsdesstub.domain.{NinoNoSuffix, TestIndividual, TestUserNotFoundException}
import uk.gov.hmrc.individualdetailsdesstub.util.JsonFormatters.formatTestUser
import uk.gov.hmrc.play.config.ServicesConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.http.{ HeaderCarrier, HttpGet, NotFoundException }

@Singleton
class ApiPlatformTestUserConnector extends ServicesConfig {
  val serviceUrl = baseUrl("api-platform-test-user")
  val http: HttpGet = WSHttp

  def getByNino(nino: Nino)(implicit hc: HeaderCarrier): Future[TestIndividual] =
    getTestIndividual(s"$serviceUrl/individuals/nino/$nino")

  def getByShortNino(shortNino: NinoNoSuffix)(implicit hc: HeaderCarrier): Future[TestIndividual] =
    getTestIndividual(s"$serviceUrl/individuals/shortnino/${shortNino.nino}")

  def getBySaUtr(saUtr: SaUtr)(implicit hc: HeaderCarrier): Future[TestIndividual] =
    getTestIndividual(s"$serviceUrl/individuals/sautr/${saUtr.utr}")

  private def getTestIndividual(url: String)(implicit hc: HeaderCarrier) =
    http.GET[TestIndividual](url) recover {
      case _: NotFoundException => throw new TestUserNotFoundException
    }

}
