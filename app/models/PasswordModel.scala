package models

import javax.inject.Inject

import anorm.Macro.ColumnNaming
import anorm._
import anorm.SqlParser._
import play.api.db.DBApi
import anorm.{Macro, RowParser}

import scala.concurrent.Future

case class Password(id: Option[Long], userId: Long, folder: Option[String], data: String)
case class PasswordRow(id: Long, folder: Option[String], data: String)
case class PasswordResponse(userId: Long, passwords: List[PasswordRow])

@javax.inject.Singleton
class PasswordModel @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {
	
	private val db = dbapi.database("default")
	
	val parser: RowParser[PasswordRow] = Macro.namedParser[PasswordRow]
	
	private def insert(password: Password) = Future(db.withConnection( implicit connection =>
		SQL("INSERT INTO passwords(user_id, folder, data) VALUES ({user_id}, {folder}, {data})")
			.on('user_id -> password.userId, 'folder -> password.folder.getOrElse("root"), 'data -> password.data)
			.executeUpdate()
	))(ec)
	
	def upsert(password: Password): Future[Int] = Future(db.withConnection(implicit connection =>
		if (password.id.isDefined) {
			Future {
				SQL("UPDATE passwords SET folder = {folder}, data = {data} WHERE id = {id}")
					.on('id -> password.id.get, 'folder -> password.folder.getOrElse("root"), 'data -> password.data)
					.executeUpdate()
			}
		}
		else {
			insert(password)
		}
	))(ec)
	.flatten

	def get(userId: Long) = Future(db.withConnection( implicit connection =>
		SQL("SELECT id, folder, data FROM passwords WHERE user_id = {user_id}")
			.on('user_id -> userId)
			.as(parser.*)
	))(ec)
}