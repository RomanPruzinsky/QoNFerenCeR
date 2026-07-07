package tr.qonferencer.backend.common

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

/** Custom exception status */
class ApiException(
	val status: HttpStatus,
	val type: String,
	override val message: String,
) : RuntimeException(message)

fun forbidden(detail: String) = ApiException(HttpStatus.FORBIDDEN, "/problems/forbidden", detail)
fun notFound(detail: String) = ApiException(HttpStatus.NOT_FOUND, "/problems/not-found", detail)
fun conflict(detail: String) = ApiException(HttpStatus.CONFLICT, "/problems/conflict", detail)
fun badRequest(detail: String) = ApiException(HttpStatus.BAD_REQUEST, "/problems/validation", detail)

/**
 * Maps [ApiException] to problem
 *
 * `@Valid` errors are handled by Spring
 */
@RestControllerAdvice
class GlobalExceptionHandler {

	@ExceptionHandler(ApiException::class)
	fun onApi(ex: ApiException): ProblemDetail = ProblemDetail
		.forStatusAndDetail(ex.status, ex.message)
		.apply { type = URI.create(ex.type) }

	@ExceptionHandler(Exception::class)
	fun onUnexpected(ex: Exception): ProblemDetail = ProblemDetail
		.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.message ?: "internal error")
		.apply { type = URI.create("/problems/internal") }
}
