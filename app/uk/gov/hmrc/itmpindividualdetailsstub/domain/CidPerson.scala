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
import uk.gov.hmrc.domain.{Nino, TaxIds}

case class CidName(firstName: String, lastName: String)

case class CidNames(current: CidName)

case class CidPerson(name: CidNames, ids: TaxIds, dateOfBirth: LocalDate)

object CidPerson {
  def apply(nino: Nino, individual: Individual): CidPerson = CidPerson(
    CidNames(CidName(individual.name.firstForenameOrInitial, individual.name.surname)),
    TaxIds(nino),
    individual.dateOfBirth
    )
}
