package models

import com.google.inject.Inject
import common.Common
import models.Tables.ThingsTable
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.Await

/**
  * Created by mijeongpark on 2017. 7. 24..
  */
class ThingRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.profile.api._

  private[models] val Things = TableQuery[ThingsTable]

  private def __findByLinkerId(linkerId: Long): DBIO[Seq[Thing]] =
    Things.filter(_.linkerId === linkerId).filter(_.deletedAt.isEmpty).result

  private def __findByMacAddress(macAddress: String): DBIO[Option[Thing]] =
    Things.filter(_.macAddress === macAddress).filter(_.deletedAt.isEmpty).result.headOption

  def findByLinkerId(linkerId: Long): Seq[Thing] =
    Await.result(db.run(__findByLinkerId(linkerId)), Common.COMMON_ASYNC_DURATION)

  def findByMacAddress(macAddress: String): Option[Thing] =
    Await.result(db.run(__findByMacAddress(macAddress)), Common.COMMON_ASYNC_DURATION)

}
