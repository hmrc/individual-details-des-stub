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

package uk.gov.hmrc.individualdetailsdesstub.controller

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, AnyContent, Result}
import uk.gov.hmrc.domain.{Nino, SaUtr}
import uk.gov.hmrc.individualdetailsdesstub.domain._
import uk.gov.hmrc.individualdetailsdesstub.service.IndividualsService
import uk.gov.hmrc.individualdetailsdesstub.util.JsonFormatters._
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.successful

@Singleton
class IndividualsController @Inject()(individualsService: IndividualsService) extends BaseController {

  def findIndividual(ninoNoSuffix: NinoNoSuffix): Action[AnyContent] = Action.async { implicit request =>
    individualsService.getIndividualByShortNino(ninoNoSuffix) map (individual => Ok(toJson(OpenidIndividual(individual)))) recover recovery
  }

  def findCidPerson(maybeNino: Option[Nino], maybeSaUtr: Option[SaUtr]): Action[AnyContent] = Action.async { implicit request =>
    (maybeNino, maybeSaUtr) match {
      case (Some(nino), _) => findCidPerson(nino)
      case (_, Some(saUtr)) => findCidPerson(saUtr)
      case (None, None) => successful(ErrorBadRequest("sautr or nino is required").toHttpResponse)
    }
  }

  private def findCidPerson(saUtr: SaUtr)(implicit hc: HeaderCarrier) = {
    individualsService.getCidPersonBySaUtr(saUtr) map (cidPerson => Ok(toJson(Seq(cidPerson)))) recover recovery
  }

  private def findCidPerson(nino: Nino)(implicit hc: HeaderCarrier) = {
    individualsService.getCidPersonByNino(nino) map (cidPerson => Ok(toJson(Seq(cidPerson)))) recover recovery
  }

  private val recovery: PartialFunction[Throwable, Result] = {
    case e: TestUserNotFoundException => ErrorNotFound(e.getMessage).toHttpResponse
    case e: Throwable =>
      Logger.error("An unexpected error occured", e)
      ErrorInternalServer.toHttpResponse
  }
}
