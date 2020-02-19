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

package uk.gov.hmrc.healthindicators.raters.readme

import javax.inject.Inject
import uk.gov.hmrc.healthindicators.configs.GithubConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, NotFoundException}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class GithubConnector @Inject()(
  httpClient: HttpClient,
  githubConfig: GithubConfig
)(implicit ec: ExecutionContext) {

  private val configKey = githubConfig.token

  def findReadMe(repo: String): Future[Option[String]] = {

    val url =
      s"${githubConfig.rawUrl}/hmrc/$repo/master/README.md"
    implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(("Authorization", s"token $configKey"))

    httpClient.GET[HttpResponse](url).map(r => Some(r.body))
  }.recoverWith {
    case e: NotFoundException => Future.successful(None)
  }
}