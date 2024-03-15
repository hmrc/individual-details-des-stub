/*
 * Copyright 2023 HM Revenue & Customs
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

package component.uk.gov.hmrc.individualdetailsdesstub.stubs

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlPathEqualTo}

import java.time.LocalDate._
import play.api.http.Status
import uk.gov.hmrc.domain.{Nino, SaUtr}
import uk.gov.hmrc.individualdetailsdesstub.domain.{CidPerson, Individual, NinoNoSuffix}

import java.time.format.DateTimeFormatter

object ApiPlatformTestUserStub extends MockHost(22001) {

  val format = DateTimeFormatter.ofPattern("ddMMyyyy")
  val outFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def getByNinoReturnsTestUserDetails(nino: Nino, cidPerson: CidPerson) = {
    mock.register(get(urlPathEqualTo(s"/individuals/nino/$nino"))
      .willReturn(aResponse().withStatus(Status.OK)
        .withBody(
          s"""{
               "userId": "945350439195",
               "password": "bLohysg8utsa",
               "saUtr": "12345",
               "nino": "$nino",
               "individualDetails": {
                 "firstName": "${cidPerson.name.current.firstName}",
                 "lastName": "${cidPerson.name.current.lastName}",
                 "dateOfBirth": "${parse(cidPerson.dateOfBirth, format).format(outFormat)}",
                 "address": {
                   "line1": "1 Abbey Road",
                   "line2": "Aberdeen"
                 }
               }
             }""")))
  }

  def getByNinoReturnsNoTestUser(nino: Nino) = {
    mock.register(get(urlPathEqualTo(s"/individuals/nino/$nino"))
      .willReturn(aResponse().withStatus(Status.NOT_FOUND)))
  }

  def getBySaUtrReturnsTestUserDetails(saUtr: SaUtr, cidPerson: CidPerson) = {
    mock.register(get(urlPathEqualTo(s"/individuals/sautr/$saUtr"))
      .willReturn(aResponse().withStatus(Status.OK)
        .withBody(
          s"""{
               "userId": "945350439195",
               "password": "bLohysg8utsa",
               "saUtr": "${saUtr.utr}",
               "nino": "AB123456A",
               "individualDetails": {
                 "firstName": "${cidPerson.name.current.firstName}",
                 "lastName": "${cidPerson.name.current.lastName}",
                 "dateOfBirth": "${parse(cidPerson.dateOfBirth, format).format(outFormat)}",
                 "address": {
                   "line1": "1 Abbey Road",
                   "line2": "Aberdeen"
                 }
               }
             }""")))
  }

  def getBySaUtrReturnsNoTestUser(saUtr: SaUtr) = {
    mock.register(get(urlPathEqualTo(s"/individuals/sautr/$saUtr"))
      .willReturn(aResponse().withStatus(Status.NOT_FOUND)))
  }

  def getByShortNinoReturnsTestUserDetails(nino: Nino, individual: Individual) = {
    mock.register(get(urlPathEqualTo(s"/individuals/shortnino/${individual.ninoNoSuffix}"))
      .willReturn(aResponse().withStatus(Status.OK)
        .withBody(
          s"""{
               "userId": "945350439195",
               "password": "bLohysg8utsa",
               "saUtr": "12345",
               "nino": "$nino",
               "individualDetails": {
                 "firstName": "${individual.name.firstForenameOrInitial}",
                 "lastName": "${individual.name.surname}",
                 "dateOfBirth": "${individual.dateOfBirth.format(outFormat)}",
                 "address": {
                   "line1": "${individual.address.line1}",
                   "line2": "${individual.address.line2}"
                 }
               }
             }""")))

  }

  def getByShortNinoReturnsNoTestUser(ninoNoSuffix: NinoNoSuffix) = {
    mock.register(get(urlPathEqualTo(s"/individuals/shortnino/${ninoNoSuffix.nino}"))
      .willReturn(aResponse().withStatus(Status.NOT_FOUND)))

  }
}
