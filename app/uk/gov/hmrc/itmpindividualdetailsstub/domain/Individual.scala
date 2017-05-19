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

case class IndividualName(firstForenameOrInitial: String,
                          surname: String,
                          secondForenameOrInitial: Option[String] = None)

case class IndividualAddress(line1: String,
                             line2: String,
                             line3: Option[String] = None,
                             line4: Option[String] = None,
                             postcode: Option[String] = None,
                             countryCode: Option[Int] = None)

case class Individual(nino: String, name: IndividualName, dateOfBirth: LocalDate, address: IndividualAddress)

object Individual {

  def apply(ninoNoSuffix: NinoNoSuffix): Individual = {
    val name = IndividualName("Amanda", "Joseph")
    val address = IndividualAddress("", "")
    Individual(ninoNoSuffix.nino, name, LocalDate.parse("2000-01-01"), address) // TODO enhance and randomise
  }

}
