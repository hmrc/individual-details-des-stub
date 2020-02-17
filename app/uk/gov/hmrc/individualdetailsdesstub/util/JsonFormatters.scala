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

package uk.gov.hmrc.individualdetailsdesstub.util

import org.joda.time.LocalDate
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Json, Writes}
import uk.gov.hmrc.domain.{SaUtr, TaxIds}
import uk.gov.hmrc.individualdetailsdesstub.domain._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

object JsonFormatters {
  implicit val shortNinoJsonFormat = Json.format[NinoNoSuffix]
  implicit val individualAddressJsonFormat = Json.format[IndividualAddress]
  implicit val individualNameJsonFormat = Json.format[IndividualName]
  implicit val individualJsonFormat = Json.format[Individual]

  implicit val formatTestUserAddress = Json.format[TestUserAddress]
  implicit val formatTestUserIndividualDetails = Json.format[TestUserIndividualDetails]
  implicit val formatTestUser = Json.format[TestIndividual]

  implicit val errorResponseWrites = new Writes[ErrorResponse] {
    def writes(e: ErrorResponse): JsValue = Json.obj("code" -> e.errorCode, "message" -> e.message)
  }

  implicit val taxIdsFormat = TaxIds.format(TaxIds.defaultSerialisableIds :_*)
  implicit val CidNameJsonFormat = Json.format[CidName]
  implicit val CidNamesJsonFormat = Json.format[CidNames]
  implicit val CidPersonJsonFormat = Json.format[CidPerson]

  implicit val openidIndividualWrite : Writes[OpenidIndividual] = (
    (JsPath \ "nino").write[String] and
      (JsPath \ "names" \ "1").write[IndividualName] and
      (JsPath \ "dateOfBirth").write[LocalDate] and
      (JsPath \ "addresses" \ "1").write[IndividualAddress]
    )(unlift(OpenidIndividual.unapply))
}
