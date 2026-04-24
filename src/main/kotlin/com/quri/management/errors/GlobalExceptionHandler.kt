package com.quri.management.errors

import com.quri.client.model.InternalFailureException
import com.quri.client.model.ResourceNotFoundException
import com.quri.client.model.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Global exception handler for all controllers across the application.
 *
 * [RestControllerAdvice] intercepts exceptions from any layer (e.g. controller, services)
 *
 * Logging Policy:
 *      - 4XX (client mistake) -> WARN, no stack trace
 *      - 5XX (server mistake) -> ERROR, with stack trace
 *
 * Never expose internal error details to clients, always return a generic message.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    // ── Smithy modeled exceptions ──────────────────────────────────────────────

    @ExceptionHandler(ValidationException::class)
    fun handleValidation(ex: ValidationException): ResponseEntity<ErrorResponse> {
        logger.warn("Validation error: {}", ex.message)
        return error(HttpStatus.BAD_REQUEST, ex.message ?: "Validation error")
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn("Resource not found: {}", ex.message)
        return error(HttpStatus.NOT_FOUND, ex.message ?: "Resource not found")
    }

    @ExceptionHandler(InternalFailureException::class)
    fun handleInternalFailure(ex: InternalFailureException): ResponseEntity<ErrorResponse> {
        logger.warn("Internal failure: {}", ex.message)
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred")
    }

    // ── Catch-all ─────────────────────────────────────────────────────────────

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unhandled exception", ex)
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred")
    }

    private fun error(
        status: HttpStatus,
        message: String,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(status)
            .body(ErrorResponse(status.value(), message))

    companion object {
        private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

        data class ErrorResponse(val status: Int, val message: String)
    }
}
