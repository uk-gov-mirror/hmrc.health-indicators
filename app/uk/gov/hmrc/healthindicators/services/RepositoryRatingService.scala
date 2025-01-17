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

package uk.gov.hmrc.healthindicators.services

import uk.gov.hmrc.healthindicators.configs.ScoreConfig
import uk.gov.hmrc.healthindicators.connectors.RepositoryType
import uk.gov.hmrc.healthindicators.models.RatingType.{BobbyRule, BuildStability, LeakDetection, ReadMe}
import uk.gov.hmrc.healthindicators.models._
import uk.gov.hmrc.healthindicators.persistence.HealthIndicatorsRepository
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class RepositoryRatingService @Inject() (repository: HealthIndicatorsRepository, scoreConfig: ScoreConfig)(implicit
  val ec: ExecutionContext
) {

  def rateRepository(repo: String): Future[Option[RepositoryRating]] =
    repository.latestRepositoryHealthIndicators(repo).map { maybeHealthIndicator =>
      rate(maybeHealthIndicator.toSeq).headOption
    }

  def rateAllRepositories(repoType: Option[RepositoryType], sort: SortType): Future[Seq[RepositoryRating]] =
    repository.latestAllRepositoryHealthIndicators(repoType).map { healthIndicators =>
      val repoRatings = rate(healthIndicators).sortBy(_.repositoryScore)
      sort match {
        case SortType.Ascending  => repoRatings
        case SortType.Descending => repoRatings.reverse
      }
    }

  def rate(healthIndicators: Seq[RepositoryHealthIndicator]): Seq[RepositoryRating] =
    for {
      indicator <- healthIndicators
      rating           = indicator.indicators.map(createRating)
      repositoryScore  = rating.map(_.ratingScore).sum
      repositoryRating = RepositoryRating(indicator.repositoryName, indicator.repositoryType, repositoryScore, rating)
    } yield repositoryRating

  private def createRating(indicator: Indicator): Rating = {
    val scores      = indicator.results.map(createScore)
    val ratingScore = scores.map(_.points).sum
    val ratingType  = translateIndicatorType(indicator.indicatorType)
    Rating(ratingType, ratingScore, scores)
  }

  private def createScore(result: Result): Score = {
    val points = scoreConfig.scores(result.resultType)
    Score(points, result.description, result.href)
  }

  private def translateIndicatorType(indicatorType: IndicatorType): RatingType =
    indicatorType match {
      case ReadMeIndicatorType         => ReadMe
      case LeakDetectionIndicatorType  => LeakDetection
      case BobbyRuleIndicatorType      => BobbyRule
      case BuildStabilityIndicatorType => BuildStability
    }
}
