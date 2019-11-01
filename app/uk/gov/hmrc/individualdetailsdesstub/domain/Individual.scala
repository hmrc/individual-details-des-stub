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

package uk.gov.hmrc.individualdetailsdesstub.domain

import org.joda.time.LocalDate
import uk.gov.hmrc.domain.SaUtr

case class IndividualName(firstForenameOrInitial: String,
                          surname: String,
                          secondForenameOrInitial: Option[String] = None)

case class IndividualAddress(line1: String,
                             line2: String,
                             line3: Option[String] = None,
                             line4: Option[String] = None,
                             postcode: Option[String] = None,
                             countryCode: Option[Int] = None)

case class Individual(ninoNoSuffix: String, name: IndividualName, dateOfBirth: LocalDate, address: IndividualAddress)

object Individual {

  def apply(ninoNoSuffix: NinoNoSuffix, testUser: TestIndividual): Individual = {
    Individual(ninoNoSuffix.nino,
      IndividualName(testUser.individualDetails.firstName, testUser.individualDetails.lastName),
      testUser.individualDetails.dateOfBirth,
      IndividualAddress(testUser.individualDetails.address.line1, testUser.individualDetails.address.line2))
  }
}

case class OpenidIndividual(ninoNoSuffix: String, name: IndividualName, dateOfBirth: LocalDate, address: IndividualAddress)

object OpenidIndividual {
  def apply(individual: Individual): OpenidIndividual = OpenidIndividual(individual.ninoNoSuffix, individual.name, individual.dateOfBirth, individual.address)
}