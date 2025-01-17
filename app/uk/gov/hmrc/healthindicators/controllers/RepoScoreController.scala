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

package uk.gov.hmrc.healthindicators.controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.healthindicators.connectors.RepositoryType
import uk.gov.hmrc.healthindicators.models.{RepositoryRating, SortType}
import uk.gov.hmrc.healthindicators.services.RepositoryRatingService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton
class RepoScoreController @Inject() (
  repoScorerService: RepositoryRatingService,
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def scoreForRepo(repo: String): Action[AnyContent] =
    Action.async {
      for {
        score <- repoScorerService.rateRepository(repo)
        result = score.map(s => Ok(Json.toJson(s)(RepositoryRating.writes))).getOrElse(NotFound)
      } yield result
    }

  def scoreAllRepos(repoType: Option[RepositoryType], sort: SortType): Action[AnyContent] = {
    implicit val writes: Writes[RepositoryRating] = RepositoryRating.writes
    Action.async {
      for {
        allRepos <- repoScorerService.rateAllRepositories(repoType, sort)
        result = Ok(Json.toJson(allRepos))
      } yield result
    }
  }
}
