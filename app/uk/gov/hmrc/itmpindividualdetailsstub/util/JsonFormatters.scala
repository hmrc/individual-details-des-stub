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

package uk.gov.hmrc.itmpindividualdetailsstub.util

import play.api.libs.json.{JsValue, Writes, Json}
import uk.gov.hmrc.domain.TaxIds
import uk.gov.hmrc.itmpindividualdetailsstub.domain._

object JsonFormatters {
  implicit val shortNinoJsonFormat = Json.format[NinoNoSuffix]
  implicit val individualAddressJsonFormat = Json.format[IndividualAddress]
  implicit val individualNameJsonFormat = Json.format[IndividualName]
  implicit val individualJsonFormat = Json.format[Individual]

  implicit val errorResponseWrites = new Writes[ErrorResponse] {
    def writes(e: ErrorResponse): JsValue = Json.obj("code" -> e.errorCode, "message" -> e.message)
  }

  implicit val taxIdsFormat = TaxIds.format(TaxIds.defaultSerialisableIds :_*)
  implicit val CidNameJsonFormat = Json.format[CidName]
  implicit val CidNamesJsonFormat = Json.format[CidNames]
  implicit val CidPersonJsonFormat = Json.format[CidPerson]
}