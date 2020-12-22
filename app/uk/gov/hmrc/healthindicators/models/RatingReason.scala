package uk.gov.hmrc.healthindicators.models

import java.time.Instant

import akka.io.Tcp.Write
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift, unaReasonpply}
import play.api.libs.json.{OFormat, Writes, __}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

case class RatingReason(
                         singleRating: Int,
                         reason: String
                       )

object RatingReason {
  val apiWrites: Writes[RatingReason] = {
    implicit val rF = RatingReason.apiWrites

    ((__ \ "singleRating").write[Int]
      ~ (__ \ "reason").write[String])(unlift(RepoScoreBreakdown.unapply))
  }
}


object RepoScoreBreakdown {

  import play.api.libs.functional.syntax.toFunctionalBuilderOps

  val apiWrites: Writes[RepoScoreBreakdown] = {
  implicit val rF = Rating.apiWrites

  ((__ \ "repo").write[String]
  ~ (__ \ "weightedScore").write[Int]
  ~ (__ \ "ratings").write[Seq[Rating]])(unlift(RepoScoreBreakdown.unapply))
}
}