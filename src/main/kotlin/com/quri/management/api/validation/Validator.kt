package com.quri.management.api.validation

import com.quri.client.model.ValidationException

/**
 * Contract for input validation. Implementations validate a single
 * Smithy input type and throw [ValidationException] on the first error.
 *
 * The [field] parameter is the top-level field name for caller (e.g. "receipt")
 * for use in error messages.
 */
fun interface Validator<T> {
    suspend fun validate(
        field: String,
        input: T,
    )
}

/**
 * Fails fast with a [ValidationException] if [condition] is false.
 * The [message] is lazily evaluated.
 */
fun require(
    condition: Boolean,
    message: () -> String,
) {
    if (!condition) {
        throw ValidationException.builder()
            .message(message())
            .build()
    }
}
