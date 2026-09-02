package tr.qonferencer.backend.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

private const val BASE = "/problems"

enum class Problem(
	val status: HttpStatus,
	val type: String,
) {
	FORBIDDEN(HttpStatus.FORBIDDEN, "$BASE/forbidden"),
	NOT_FOUND(HttpStatus.NOT_FOUND, "$BASE/not-found"),
	CONFLICT(HttpStatus.CONFLICT, "$BASE/conflict"),
	VALIDATION(HttpStatus.BAD_REQUEST, "$BASE/validation"),
	INTERNAL(HttpStatus.INTERNAL_SERVER_ERROR, "$BASE/internal"),
}

/** Custom exception status */
class ApiException(
	val problem: Problem,
	override val message: String,
) : RuntimeException(message)

fun forbidden(detail: String) = ApiException(Problem.FORBIDDEN, detail)
fun notFound(detail: String) = ApiException(Problem.NOT_FOUND, detail)
fun conflict(detail: String) = ApiException(Problem.CONFLICT, detail)
fun badRequest(detail: String) = ApiException(Problem.VALIDATION, detail)

/** Maps [ApiException] to problem */
@RestControllerAdvice
class GlobalExceptionHandler {

	@ExceptionHandler(ApiException::class)
	fun onApi(ex: ApiException): ProblemDetail = ProblemDetail
		.forStatusAndDetail(ex.problem.status, ex.message)
		.apply { type = URI.create(ex.problem.type) }

	/** Logged unexpected error */
	@ExceptionHandler(Exception::class)
	fun onUnexpected(ex: Exception): ProblemDetail {
		log.error("unhandled exception", ex)
		return ProblemDetail
			.forStatusAndDetail(Problem.INTERNAL.status, "internal error")
			.apply { type = URI.create(Problem.INTERNAL.type) }
	}

	private companion object {
		val log: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
	}
}
