package com.quri.management.api.errors

import com.quri.client.model.InternalFailureException
import com.quri.client.model.ResourceNotFoundException
import com.quri.client.model.ValidationException
import com.quri.management.api.errors.GlobalExceptionHandler.Companion.ErrorResponse
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebInputException

@Suppress("unused")
class GlobalExceptionHandlerTest :
    DescribeSpec({

        val handler = GlobalExceptionHandler()

        describe("handleValidation") {

            context("when ValidationException has a message") {
                it("returns 400 with the exception message") {
                    val ex = ValidationException.builder().message("name is required").build()

                    val response = handler.handleValidation(ex)

                    assertSoftly(response) {
                        it.statusCode shouldBe HttpStatus.BAD_REQUEST
                        it.body shouldBe ErrorResponse(400, "name is required")
                    }
                }
            }

            context("when ValidationException has no message") {
                it("returns 400 with default message") {
                    val ex = ValidationException.builder().build()

                    val response = handler.handleValidation(ex)

                    assertSoftly(response) {
                        it.statusCode shouldBe HttpStatus.BAD_REQUEST
                        it.body shouldBe ErrorResponse(400, "Validation error")
                    }
                }
            }
        }

        describe("handleNotFound") {

            context("when ResourceNotFoundException has a message") {
                it("returns 404 with the exception message") {
                    val ex = ResourceNotFoundException.builder()
                        .message("Bill with ID `bill-1` not found")
                        .build()

                    val response = handler.handleNotFound(ex)

                    assertSoftly(response) {
                        it.statusCode shouldBe HttpStatus.NOT_FOUND
                        it.body shouldBe ErrorResponse(404, "Bill with ID `bill-1` not found")
                    }
                }
            }

            context("when ResourceNotFoundException has no message") {
                it("returns 404 with default message") {
                    val ex = ResourceNotFoundException.builder().build()

                    val response = handler.handleNotFound(ex)

                    assertSoftly(response) {
                        it.statusCode shouldBe HttpStatus.NOT_FOUND
                        it.body shouldBe ErrorResponse(404, "Resource not found")
                    }
                }
            }
        }

        describe("handleInternalFailure") {

            it("returns 500 with a safe generic message regardless of exception message") {
                val ex = InternalFailureException.builder()
                    .message("Mongo connection failed — internal detail")
                    .build()

                val response = handler.handleInternalFailure(ex)

                assertSoftly(response) {
                    it.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
                    it.body shouldBe ErrorResponse(500, "An unexpected error occurred")
                }
            }
        }

        describe("handleException") {

            it("returns 500 with a safe generic message for any unhandled exception") {
                val ex = RuntimeException("Something exploded")

                val response = handler.handleException(ex)

                assertSoftly(response) {
                    it.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
                    it.body shouldBe ErrorResponse(500, "An unexpected error occurred")
                }
            }
        }

        describe("handleWebInput") {

            context("when cause chain contains a ValidationException") {
                it("returns 400 with the ValidationException message") {
                    val validationEx = ValidationException.builder()
                        .message("amount must be between 0 and 1")
                        .build()
                    val ex = ServerWebInputException("Bad input", null, validationEx)

                    val response = handler.handleWebInput(ex)

                    assertSoftly(response) {
                        it.statusCode shouldBe HttpStatus.BAD_REQUEST
                        it.body shouldBe ErrorResponse(400, "amount must be between 0 and 1")
                    }
                }
            }

            context("when cause chain contains a nested ValidationException") {
                it("walks the cause chain and returns the ValidationException message") {
                    val validationEx = ValidationException.builder()
                        .message("currency must be a valid ISO 4217 code")
                        .build()
                    val wrappingEx = RuntimeException("wrapped", validationEx)
                    val ex = ServerWebInputException("Bad input", null, wrappingEx)

                    val response = handler.handleWebInput(ex)

                    assertSoftly(response) {
                        it.statusCode shouldBe HttpStatus.BAD_REQUEST
                        it.body shouldBe ErrorResponse(400, "currency must be a valid ISO 4217 code")
                    }
                }
            }

            context("when cause has a plain message but no ValidationException") {
                it("returns 400 with the cause message") {
                    val cause = IllegalArgumentException("Malformed JSON field")
                    val ex = ServerWebInputException("Bad input", null, cause)

                    val response = handler.handleWebInput(ex)

                    assertSoftly(response) {
                        it.statusCode shouldBe HttpStatus.BAD_REQUEST
                        it.body shouldBe ErrorResponse(400, "Malformed JSON field")
                    }
                }
            }

            context("when there is no cause") {
                it("returns 400 with the default message") {
                    val ex = ServerWebInputException("Bad input")

                    val response = handler.handleWebInput(ex)

                    assertSoftly(response) {
                        it.statusCode shouldBe HttpStatus.BAD_REQUEST
                        it.body shouldBe ErrorResponse(400, "Malformed request body")
                    }
                }
            }
        }
    })
