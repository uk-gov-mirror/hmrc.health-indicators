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

package uk.gov.hmrc.healthindicators.services

import java.time.Instant

import org.mockito.MockitoSugar
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.healthindicators.models.{HealthIndicators, RatingType}
import uk.gov.hmrc.healthindicators.raters.leakdetection.LeakDetectionRating
import uk.gov.hmrc.healthindicators.raters.readme.ReadMeRating
import uk.gov.hmrc.healthindicators.raters.readme.ReadMeType.ValidReadMe

class WeightServiceSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "weightedScore" should {

    "Return a Weighted Score based on Ratings and Weights" in {

      val weights = Map[RatingType, Double](RatingType.ReadMe -> 2.0, RatingType.LeakDetection -> 1.0)

      val result100 = WeightService.weightedScoreInternal(weights)(TestData.healthIndicator100)
      val result67  = WeightService.weightedScoreInternal(weights)(TestData.healthIndicator67)

      result100 mustBe 100
      result67 mustBe 67
    }
  }
}

object TestData {
  val healthIndicator100 =
    HealthIndicators("foo", Instant.now(), Seq(ReadMeRating(ValidReadMe), LeakDetectionRating(0)))
  val healthIndicator67 =
    HealthIndicators("bar", Instant.now(), Seq(ReadMeRating(ValidReadMe), LeakDetectionRating(4)))
}