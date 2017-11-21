package models

import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import play.api.db.DBApi
import scala.concurrent.Future

case class CreateAccount(firstName: String, lastName: String, email: String, verified: Boolean, password: String)
case class LoginRequest(email: String, password: String)

@javax.inject.Singleton
class AccountModel @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {
	
	private val db = dbapi.database("default")
	
	def insert(createAccount: CreateAccount) = Future(db.withConnection( implicit connection =>
		SQL("INSERT INTO accounts(first_name, last_name, email, password) VALUES ({first_name}, {last_name}, {email}, {password})")
			.on('first_name -> createAccount.firstName, 'last_name -> createAccount.lastName, 'email -> createAccount.email, 'password -> createAccount.password)
			.executeUpdate()
	))(ec)
	
	def get(loginRequest: LoginRequest) = Future(db.withConnection( implicit connection =>
		SQL("SELECT id FROM accounts WHERE email = {email} AND password = {password}")
			.on('email -> loginRequest.email, 'password -> loginRequest.password)
			.as(SqlParser.int("id").singleOpt)
	))(ec)
}