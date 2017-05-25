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

import play.api.libs.json.Json.toJson
import play.api.mvc.{Result, Action, AnyContent}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.individualdetailsdesstub.domain.{ErrorNotFound, OpenidIndividual, ErrorInternalServer, NinoNoSuffix}
import uk.gov.hmrc.individualdetailsdesstub.service.IndividualsService
import uk.gov.hmrc.individualdetailsdesstub.util.JsonFormatters._
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.successful

@Singleton
class IndividualsController @Inject()(individualsService: IndividualsService) extends BaseController {

  def fetchOrCreateIndividual(ninoNoSuffix: NinoNoSuffix): Action[AnyContent] = Action.async {
    individualsService.read(ninoNoSuffix) flatMap {
      case Some(individual) => successful(individual)
      case None => individualsService.create(ninoNoSuffix)
    } map (individual => Ok(toJson(OpenidIndividual(individual)))) recover recovery
  }

  def findCidPerson(nino: Nino): Action[AnyContent] = Action.async {
    individualsService.getCidPerson(nino) map {
      case Some(cidPerson) => Ok(toJson(Seq(cidPerson)))
      case None => ErrorNotFound.toHttpResponse
    } recover recovery
  }

  private val recovery: PartialFunction[Throwable, Result] = {
    case _ => ErrorInternalServer.toHttpResponse
  }
}
