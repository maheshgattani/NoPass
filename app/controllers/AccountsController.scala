package controllers

import javax.inject._

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
import models._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class PasswordRequest(email: String, password: String, folder: Option[String], passwordId: Option[Long], data: String)

/**
 * Controller to handle all things "Accounts" and it's passwords
 */
@Singleton
class AccountsController @Inject()(cc: ControllerComponents, accountModel: AccountModel, passwordModel: PasswordModel) extends AbstractController(cc) {
	
	private val logger = play.api.Logger(this.getClass)
	
	implicit val accountReads: Reads[CreateAccount] = (
		(JsPath \ "first_name").read[String](minLength[String](2) keepAnd (maxLength[String](255))) and
		(JsPath \ "last_name").read[String](minLength[String](2) keepAnd (maxLength[String](255))) and
		(JsPath \ "email").read[String](email keepAnd (maxLength[String](255))) and
		Reads.pure(false) and
		(JsPath \ "password").read[String](minLength[String](12) keepAnd (maxLength[String](255)))
	) (CreateAccount.apply _)
	
	implicit val loginReads: Reads[LoginRequest] = (
			(JsPath \ "email").read[String](email keepAnd (maxLength[String](255))) and
			(JsPath \ "password").read[String](minLength[String](12) keepAnd (maxLength[String](255)))
		) (LoginRequest.apply _)
	
	implicit val passwordReads: Reads[Password] = (
			(JsPath \ "id").readNullable[Long] and
			(JsPath \ "user_id").read[Long] and
			(JsPath \ "folder").readNullable[String](maxLength[String](255)) and
			(JsPath \ "data").read[String]
		) (Password.apply _)
	
	implicit val passwordRequestReads: Reads[PasswordRequest] = (
			(JsPath \ "email").read[String](email keepAnd (maxLength[String](255))) and
			(JsPath \ "password").read[String](minLength[String](12) keepAnd (maxLength[String](255))) and
			(JsPath \ "folder").readNullable[String](minLength[String](12) keepAnd (maxLength[String](255))) and
			(JsPath \ "password_id").readNullable[Long] and
			(JsPath \ "data").read[String]
		) (PasswordRequest.apply _)
	
	implicit val passwordPartialWrites: Writes[PasswordRow] = (
			(JsPath \ "id").write[Long] and
			(JsPath \ "folder").writeNullable[String] and
			(JsPath \ "data").write[String]
		)(unlift(PasswordRow.unapply))
	
	implicit val passwordResponseWrites: Writes[PasswordResponse] = (
			(JsPath \ "userId").write[Long] and
			(JsPath \ "passwords").write[List[PasswordRow]]
		)(unlift(PasswordResponse.unapply))
	
	def validateJson[A : Reads] = parse.json.validate(
		_.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
	)
	
	def createAccount() = Action.async(validateJson[CreateAccount]) { request =>
		val createAccount = request.body
		accountModel.insert(createAccount).map { _ =>
			Ok(Json.obj("status" -> "Ok", "message" -> ("User '" + createAccount.email + "' created.")))
		}.recover {
			case ex: MySQLIntegrityConstraintViolationException =>
				BadRequest(Json.obj("status" -> "Error", "message" -> ("Username or email already exists.")))
			case ex =>
				logger.error("Exception raised", ex)
				InternalServerError
		}
	}
	
	def login() = Action.async(validateJson[LoginRequest]) { request =>
		val loginRequest = request.body
		accountModel.get(loginRequest).map { userIdOption =>
			if (userIdOption.isDefined) {
				passwordModel.get(userIdOption.get).map( passwords => {
					val response = PasswordResponse(userIdOption.get, passwords)
					Ok(Json.toJson(response))
				})
				.recover {
					case ex =>
						logger.error("Exception raised", ex)
						InternalServerError
				}
			}
			else
				Future(BadRequest(Json.obj("status" -> "Error", "message" -> ("username or password not correct"))))
		}.recover {
			case ex =>
				logger.error("Exception raised", ex)
				Future(InternalServerError)
		}.flatten
	}
	
	def upsertPassword() = Action.async(validateJson[PasswordRequest]) { request =>
		val passwordRequest = request.body
		val loginRequest = LoginRequest(passwordRequest.email, passwordRequest.password)
		accountModel.get(loginRequest).map { userIdOption =>
			if (userIdOption.isDefined) {
				val password = Password(passwordRequest.passwordId, userIdOption.get, passwordRequest.folder, passwordRequest.data)
				passwordModel.upsert(password).map( _ =>
					passwordModel.get(userIdOption.get).map( passwords => {
						val response = PasswordResponse(userIdOption.get, passwords)
						Ok(Json.toJson(response))
					})
					.recover {
						case ex =>
							logger.error("Exception raised", ex)
							InternalServerError
					}
				)
				.recover {
					case ex =>
						logger.error("Exception raised", ex)
						Future(InternalServerError)
				}.flatten
			}
			else
				Future(BadRequest(Json.obj("status" -> "Error", "message" -> ("username or password not correct"))))
		}.recover {
			case ex =>
				logger.error("Exception raised", ex)
				Future(InternalServerError)
		}.flatten
	}
}
