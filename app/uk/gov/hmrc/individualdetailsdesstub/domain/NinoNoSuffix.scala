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

package uk.gov.hmrc.individualdetailsdesstub.domain

import play.api.libs.json.{Reads, Writes}
import uk.gov.hmrc.domain._

case class NinoNoSuffix(nino: String) extends TaxIdentifier with SimpleName {
  require(NinoNoSuffix.isValid(nino), s"$nino is not a valid nino.")
  override def value: String = nino
  override val name: String = "nino-no-suffix"
}

object NinoNoSuffix extends (String => NinoNoSuffix) {
  implicit val ninoWrite: Writes[NinoNoSuffix] = new SimpleObjectWrites[NinoNoSuffix](_.value)
  implicit val ninoRead: Reads[NinoNoSuffix] = new SimpleObjectReads[NinoNoSuffix]("nino-no-suffix", NinoNoSuffix.apply)

  def isValid(nino: String) = nino != null && Nino.isValid(nino + "A")

  def apply(nino: Nino): NinoNoSuffix = NinoNoSuffix(nino.nino.substring(0, nino.nino.length - 1))
}
