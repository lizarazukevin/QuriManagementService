package com.quri.management.api.validation

import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.Instant

// ════════════════════════════════════════════════════════════════════
// Reusable field validators — extension functions on primitives
// ════════════════════════════════════════════════════════════════════

/**
 * Requires a non-null value. Returns the unwrapped value for chaining.
 *
 * ```
 * input.name.validateRequired("name").validateLength("name", max = 100)
 * ```
 */
fun <T : Any> T?.validateRequired(field: String): T {
    require(this != null) { "$field is required" }
    return this!!
}

/**
 * Validates string length is within [min]..[max]. Passes through the
 * validated string so it can be chained.
 */
fun String?.validateLength(
    field: String,
    min: Int = 0,
    max: Int,
): String {
    val value = validateRequired(field)
    require(value.length in min..max) { "$field must be $min-$max characters" }
    return value
}

/**
 * Validates a string matches the given [pattern]. The [hint] should
 * describe the expected format (e.g., "must be a valid ISO 4217 code").
 */
fun String?.validatePattern(
    field: String,
    pattern: Regex,
    hint: String,
): String? {
    val value = validateRequired(field)
    require(value.matches(pattern)) { "$field $hint" }
    return this
}

/**
 * Validates a [BigDecimal] is in the range `0..1`, inclusive.
 */
fun BigDecimal?.validateRate(field: String): BigDecimal? {
    val value = validateRequired(field)
    require(value in BigDecimal.ZERO..BigDecimal.ONE) { "$field must be between 0 and 1" }
    return this
}

/**
 * Validates an integer is within [min]..[max].
 */
fun Int?.validateInteger(
    field: String,
    min: Int = 0,
    max: Int,
): Int? {
    val value = validateRequired(field)
    require(value in min..max) { "$field must be between $min and $max" }
    return this
}

/**
 * Validates an [Instant] is in the past (strictly before now).
 */
fun Instant?.validateTimestamp(field: String): Instant? {
    val value = validateRequired(field)
    require(value < Instant.now()) { "$field must not occur in the future" }
    return this
}

/**
 * Validates a list of [ObjectId]s for objects referencing Mongo documents.
 */
fun List<String>?.validateObjectIdList(field: String): List<ObjectId> {
    val value = validateRequired(field)
    value.forEachIndexed { index, id ->
        require(ObjectId.isValid(id)) {
            "$field[$index] is not a valid ObjectId: $id"
        }
    }
    return value.map(::ObjectId)
}
