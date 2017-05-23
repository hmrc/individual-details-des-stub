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

package uk.gov.hmrc.itmpindividualdetailsstub.domain

import org.joda.time.LocalDate
import uk.gov.hmrc.itmpindividualdetailsstub.util.Randomiser

case class IndividualName(firstForenameOrInitial: String,
                          surname: String,
                          secondForenameOrInitial: Option[String] = None)

object IndividualName extends Randomiser {
  def apply(): IndividualName = IndividualName(
    randomConfigString("randomiser.individualName.forename"),
    randomConfigString("randomiser.individualName.surname")
  )
}

case class IndividualAddress(line1: String,
                             line2: String,
                             line3: Option[String] = None,
                             line4: Option[String] = None,
                             postcode: Option[String] = None,
                             countryCode: Option[Int] = None)

object IndividualAddress extends Randomiser {
  def apply(): IndividualAddress =
    IndividualAddress(
      randomConfigString("randomiser.individualAddress.line1"),
      randomConfigString("randomiser.individualAddress.line2")
    )
}

case class Individual(nino: String, name: IndividualName, dateOfBirth: LocalDate, address: IndividualAddress)

object Individual extends Randomiser {

  def apply(ninoNoSuffix: NinoNoSuffix): Individual = {
    Individual(ninoNoSuffix.nino, IndividualName(), randomNinoEligibleDateOfBirth(), IndividualAddress())
  }

}
