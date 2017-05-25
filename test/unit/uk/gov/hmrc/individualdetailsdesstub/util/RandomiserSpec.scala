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

package unit.uk.gov.hmrc.individualdetailsdesstub.util

import org.joda.time.LocalDate
import uk.gov.hmrc.individualdetailsdesstub.util.Randomiser
import uk.gov.hmrc.play.test.UnitSpec

class RandomiserSpec extends UnitSpec {
  implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toDate.getTime)

  trait Setup {
    val underTest = new Randomiser("testRandomiser")
  }

  "randomNinoEligibleDateOfBirth" should {
    "return a random date" in new Setup {
      val date1 = underTest.randomNinoEligibleDateOfBirth()
      val date2 = underTest.randomNinoEligibleDateOfBirth()

      date1 shouldNot be (date2)
    }

    "return a date more than 16 years old" in new Setup {
      val date = underTest.randomNinoEligibleDateOfBirth()

      date should be <= LocalDate.now().minusYears(16)
    }

    "return a date less than 100 years old" in new Setup {
      val date = underTest.randomNinoEligibleDateOfBirth()

      date should be >= LocalDate.now().minusYears(101)
    }
  }

  "randomConfigString" should {
    "return the property from the properties file when there is only one entry" in new Setup {
      underTest.randomConfigString("randomiser.oneEntry") shouldBe "entry"
    }

    "pick randomly from the property file when there are multiple entries" in new Setup {
      underTest.randomConfigString("randomiser.twoEntries") should (equal ("entry1") or equal ("entry2"))
    }
  }
}
