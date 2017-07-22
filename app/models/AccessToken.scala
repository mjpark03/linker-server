package models

import Tables._
import javax.inject.Inject

import common.Common
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{Await}

/**
  * Created by Rachel on 2017. 7. 13..
  */
class AccessTokenRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import scala.concurrent.ExecutionContext.Implicits.global
  import dbConfig.profile.api._

  private[models] val AccessTokens = TableQuery[AccessTokensTable]

  private def __findByAccessToken(accessToken: String): DBIO[Option[AccessToken]] =
    AccessTokens.filter(_.accessToken === accessToken).result.headOption

  private def __findByCustomerDeviceId(customerDeviceId: Long): DBIO[Option[AccessToken]] =
    AccessTokens.filter(_.customerDeviceId === customerDeviceId).result.headOption

  private def __insert(accessToken: AccessToken): DBIO[Long] =
    AccessTokens returning AccessTokens.map(_.id) += accessToken

  def findByAccessToken(accessToken: String): Option[AccessToken] = {
    Await.result(db.run(__findByAccessToken(accessToken)), Common.COMMON_ASYNC_DURATION)
  }

  def findByCustomerDeviceId(customerDeviceId: Long): Option[AccessToken] = {
    Await.result(db.run(__findByCustomerDeviceId(customerDeviceId)), Common.COMMON_ASYNC_DURATION)
  }

  def insertAccessToken(accessToken: AccessToken): Long = {
    Await.result(db.run(__insert(accessToken)), Common.COMMON_ASYNC_DURATION)
  }
}
