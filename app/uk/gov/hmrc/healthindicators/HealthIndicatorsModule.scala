/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.healthindicators

import com.google.inject.{AbstractModule, Provides}
import play.api.Logger
import uk.gov.hmrc.healthindicators.raters.{BobbyRulesRater, BuildStabilityRater, LeakDetectionRater, Rater, ReadMeRater}

class HealthIndicatorsModule() extends AbstractModule {

  private val logger = Logger(this.getClass)

  override def configure(): Unit =
    bind(classOf[schedulers.RepoRatingsScheduler]).asEagerSingleton()

  @Provides
  def raters(
    bobbyRulesRater: BobbyRulesRater,
    leakDetectionRater: LeakDetectionRater,
    readMeRater: ReadMeRater,
    buildStabilityRater: BuildStabilityRater
  ): List[Rater] = {
    val raters = List(bobbyRulesRater, leakDetectionRater, readMeRater, buildStabilityRater)
    logger.info(s"Loaded Raters: ${raters.map(_.getClass.getSimpleName).mkString("[\n", "\n", "\n]")}")
    raters
  }
}
