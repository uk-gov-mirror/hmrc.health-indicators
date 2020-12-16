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

package uk.gov.hmrc.healthindicators.raters.bobbyrules

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.healthindicators.configs.RatersConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BobbyRuleConnector @Inject()(
  httpClient: HttpClient,
  healthIndicatorsConfig: RatersConfig

)(implicit val ec: ExecutionContext) {

    private implicit val hc = HeaderCarrier()

    private val bobbyRuleBaseURL: String = healthIndicatorsConfig.bobbyRuleUrl

    def findLatestMasterReport(repo: String): Future[Option[Report]] = {
        implicit val rF = Report.reads

        httpClient.GET[Option[Report]](
            bobbyRuleBaseURL
                + s"/api/dependencies/"
                + repo)

    }
}