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

package uk.gov.hmrc.individualdetailsdesstub

import play.api.mvc.QueryStringBindable
import uk.gov.hmrc.domain.{Nino, SaUtr}
import uk.gov.hmrc.individualdetailsdesstub.domain.NinoNoSuffix
import uk.gov.hmrc.play.binders.SimpleObjectBinder

object NinoNoSuffixBinder extends SimpleObjectBinder[NinoNoSuffix](NinoNoSuffix.apply, _.value)

package object Binders {

  implicit val ninoQueryStringBinder = new QueryStringBindable[Nino] {
    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Nino]] = try {
      params.get(key).flatMap(_.headOption).map(value => Right(Nino(value)))
    } catch {
      case e: Throwable => Some(Left(s"Cannot parse parameter '$key' with parameters '$params' as 'NINO'"))
    }

    def unbind(key: String, value: Nino): String = QueryStringBindable.bindableString.unbind(key, value.nino)
  }

  implicit val saUtrQueryStringBinder = new QueryStringBindable[SaUtr] {
    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SaUtr]] = try {
      params.get(key).flatMap(_.headOption).map(value => Right(SaUtr(value)))
    } catch {
      case e: Throwable => Some(Left(s"Cannot parse parameter '$key' with parameters '$params' as 'SA UTR'"))
    }

    def unbind(key: String, value: SaUtr): String = QueryStringBindable.bindableString.unbind(key, value.utr)
  }

  implicit val ninoNoSuffixBinder = NinoNoSuffixBinder

}
