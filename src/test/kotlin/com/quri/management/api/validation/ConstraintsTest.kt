package com.quri.management.api.validation

import com.quri.client.model.ValidationException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.Instant

@Suppress("unused")
class ConstraintsTest :
    DescribeSpec({

        describe("validateRequired") {

            it("returns the value when non-null") {
                val result = "value".validateRequired("field")
                result shouldBe "value"
            }

            it("returns the value when null") {
                val value: String? = null
                shouldThrow<ValidationException> {
                    value.validateRequired("field")
                }
            }
        }

        describe("validateLength") {

            context("when min value isn't specified") {
                it("returns the value") {
                    val result = "".validateLength("field", max = 10)
                    result shouldBe ""
                }
            }

            context("when value is within range") {
                it("returns the value") {
                    val result = "hello".validateLength("field", min = 1, max = 10)
                    result shouldBe "hello"
                }
            }

            context("when value is at min boundary") {
                it("passes") {
                    "a".validateLength("field", min = 1, max = 10) shouldBe "a"
                }
            }

            context("when value is at max boundary") {
                it("passes") {
                    "hellohello".validateLength("field", min = 1, max = 10) shouldBe "hellohello"
                }
            }

            context("when value is below min") {
                it("throws ValidationException") {
                    shouldThrow<ValidationException> {
                        "".validateLength("field", min = 1, max = 10)
                    }
                }
            }

            context("when value is above max") {
                it("throws ValidationException") {
                    shouldThrow<ValidationException> {
                        "hellohello!".validateLength("field", min = 1, max = 10)
                    }
                }
            }

            context("when value is null") {
                it("throws ValidationException") {
                    val value: String? = null
                    shouldThrow<ValidationException> {
                        value.validateLength("field", min = 1, max = 10)
                    }
                }
            }
        }

        describe("validatePattern") {

            context("when value matches pattern") {
                it("returns the value") {
                    val result = "USD".validatePattern("field", Regex("^[A-Z]{3}$"), "must be 3 uppercase letters")
                    result shouldBe "USD"
                }
            }

            context("when value does not match pattern") {
                it("throws ValidationException") {
                    shouldThrow<ValidationException> {
                        "usd".validatePattern("field", Regex("^[A-Z]{3}$"), "must be 3 uppercase letters")
                    }
                }
            }

            context("when value is null") {
                it("throws ValidationException") {
                    val value: String? = null
                    shouldThrow<ValidationException> {
                        value.validatePattern("field", Regex("^[A-Z]{3}$"), "must be 3 uppercase letters")
                    }
                }
            }
        }

        describe("validateRate") {

            context("when value is within 0..1") {
                it("passes at zero") {
                    BigDecimal.ZERO.validateRate("field") shouldBe BigDecimal.ZERO
                }

                it("passes at one") {
                    BigDecimal.ONE.validateRate("field") shouldBe BigDecimal.ONE
                }

                it("passes at mid range") {
                    BigDecimal("0.5").validateRate("field") shouldBe BigDecimal("0.5")
                }
            }

            context("when value is below zero") {
                it("throws ValidationException") {
                    shouldThrow<ValidationException> {
                        BigDecimal("-0.01").validateRate("field")
                    }
                }
            }

            context("when value is above one") {
                it("throws ValidationException") {
                    shouldThrow<ValidationException> {
                        BigDecimal("1.01").validateRate("field")
                    }
                }
            }

            context("when value is null") {
                it("throws ValidationException") {
                    val value: BigDecimal? = null
                    shouldThrow<ValidationException> {
                        value.validateRate("field")
                    }
                }
            }
        }

        describe("validateInteger") {

            context("when min value isn't specified") {
                it("returns the value") {
                    0.validateInteger("field", max = 10) shouldBe 0
                }
            }

            context("when value is within range") {
                it("passes at min boundary") {
                    1.validateInteger("field", min = 1, max = 100) shouldBe 1
                }

                it("passes at max boundary") {
                    100.validateInteger("field", min = 1, max = 100) shouldBe 100
                }
            }

            context("when value is below min") {
                it("throws ValidationException") {
                    shouldThrow<ValidationException> {
                        0.validateInteger("field", min = 1, max = 100)
                    }
                }
            }

            context("when value is above max") {
                it("throws ValidationException") {
                    shouldThrow<ValidationException> {
                        101.validateInteger("field", min = 1, max = 100)
                    }
                }
            }

            context("when value is null") {
                it("throws ValidationException") {
                    val value: Int? = null
                    shouldThrow<ValidationException> {
                        value.validateInteger("field", min = 1, max = 100)
                    }
                }
            }
        }

        describe("validateTimestamp") {

            context("when instant is in the past") {
                it("passes") {
                    val past = Instant.now().minusSeconds(3600)
                    past.validateTimestamp("field") shouldNotBe null
                }
            }

            context("when instant is in the future") {
                it("throws ValidationException") {
                    val future = Instant.now().plusSeconds(3600)
                    shouldThrow<ValidationException> {
                        future.validateTimestamp("field")
                    }
                }
            }

            context("when value is null") {
                it("throws ValidationException") {
                    val value: Instant? = null
                    shouldThrow<ValidationException> {
                        value.validateTimestamp("field")
                    }
                }
            }
        }

        describe("validateObjectIdList") {

            context("when all entries are valid ObjectIds") {
                it("returns the mapped ObjectId list") {
                    val id1 = ObjectId()
                    val id2 = ObjectId()
                    val result = listOf(id1.toString(), id2.toString()).validateObjectIdList("field")
                    result shouldBe listOf(id1, id2)
                }
            }

            context("when list contains an invalid ObjectId") {
                it("throws ValidationException") {
                    shouldThrow<ValidationException> {
                        listOf("not-an-objectid").validateObjectIdList("field")
                    }
                }
            }

            context("when list is empty") {
                it("returns empty list") {
                    val result = emptyList<String>().validateObjectIdList("field")
                    result shouldBe emptyList()
                }
            }

            context("when value is null") {
                it("throws ValidationException") {
                    val value: List<String>? = null
                    shouldThrow<ValidationException> {
                        value.validateObjectIdList("field")
                    }
                }
            }
        }

        describe("validateObjectId") {

            context("when an object id is a valid ObjectId") {
                it("returns an object id") {
                    val id = ObjectId()
                    val result = id.toString().validateObjectId("field")
                    result shouldBe id
                }
            }

            context("when an object id is not a valid ObjectId") {
                it("throws ValidationException") {
                    val id = "fake-id"
                    shouldThrow<ValidationException> {
                        id.validateObjectId("field")
                    }
                }
            }
        }
    })
