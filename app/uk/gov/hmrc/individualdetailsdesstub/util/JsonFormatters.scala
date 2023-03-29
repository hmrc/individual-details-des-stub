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
import play.api.libs.json.JodaWrites._
import play.api.libs.json._
import uk.gov.hmrc.domain.TaxIds
import uk.gov.hmrc.individualdetailsdesstub.domain._

object JsonFormatters {
  implicit val yodaFormat: Reads[LocalDate] = play.api.libs.json.JodaReads.DefaultJodaLocalDateReads

  implicit val shortNinoJsonFormat: OFormat[NinoNoSuffix] = Json.format[NinoNoSuffix]
  implicit val individualAddressJsonFormat: OFormat[IndividualAddress] = Json.format[IndividualAddress]
  implicit val individualNameJsonFormat: OFormat[IndividualName] = Json.format[IndividualName]
  implicit val individualJsonFormat: OFormat[Individual] = Json.format[Individual]

  implicit val formatTestUserAddress: OFormat[TestUserAddress] = Json.format[TestUserAddress]
  implicit val formatTestUserIndividualDetails: OFormat[TestUserIndividualDetails] = Json.format[TestUserIndividualDetails]
  implicit val formatTestUser: OFormat[TestIndividual] = Json.format[TestIndividual]

  implicit val errorResponseWrites: Writes[ErrorResponse] = new Writes[ErrorResponse] {
    def writes(e: ErrorResponse): JsValue = Json.obj("code" -> e.errorCode, "message" -> e.message)
  }

  implicit val taxIdsFormat: Format[TaxIds] = TaxIds.format(TaxIds.defaultSerialisableIds: _*)
  implicit val CidNameJsonFormat: OFormat[CidName] = Json.format[CidName]
  implicit val CidNamesJsonFormat: OFormat[CidNames] = Json.format[CidNames]
  implicit val CidPersonJsonFormat: OFormat[CidPerson] = Json.format[CidPerson]

  implicit val openidIndividualWrite: Writes[OpenidIndividual] = (
    (JsPath \ "nino").write[String] and
      (JsPath \ "names" \ "1").write[IndividualName] and
      (JsPath \ "dateOfBirth").write[LocalDate] and
      (JsPath \ "addresses" \ "1").write[IndividualAddress]
    )(unlift(OpenidIndividual.unapply))
}
