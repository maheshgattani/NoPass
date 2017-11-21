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

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class OptionsController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
	
	def headers = List(
		"Access-Control-Allow-Origin" -> "*",
		"Access-Control-Allow-Methods" -> "GET, POST, OPTIONS, DELETE, PUT",
		"Access-Control-Max-Age" -> "3600",
		"Access-Control-Allow-Headers" -> "Origin, Content-Type, Accept, Authorization",
		"Access-Control-Allow-Credentials" -> "true"
	)
	
	def rootOptions = options("/")
	def options(url: String) = Action { request =>
		NoContent.withHeaders(headers : _*)
	}
}
