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

package uk.gov.hmrc.itmpindividualdetailsstub.controller

import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, AnyContent, Controller}
import uk.gov.hmrc.itmpindividualdetailsstub.domain.ShortNino
import uk.gov.hmrc.itmpindividualdetailsstub.service.IndividualsService
import uk.gov.hmrc.itmpindividualdetailsstub.util.JsonFormatters._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.successful

@Singleton
class IndividualsController @Inject()(individualsService: IndividualsService) extends Controller {

  def get(shortNinoString: String): Action[AnyContent] = Action.async {
    val shortNino = ShortNino(shortNinoString)
    individualsService.read(shortNino) flatMap {
      case Some(individual) => successful(individual)
      case None => individualsService.create(shortNino)
    } map (individual => Ok(toJson(individual)))
  }

}
