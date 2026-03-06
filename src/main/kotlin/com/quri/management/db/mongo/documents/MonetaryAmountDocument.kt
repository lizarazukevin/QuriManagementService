package com.quri.management.db.mongo.documents

import java.math.BigDecimal

/**
 * Embedded monetary value document.
 *
 * [BigDecimal] is used over [Double] or [Float] to avoid floating-point
 * precision errors. Never use floating-point types for monetary values.
 *
 * @param amount numerical value
 * @param currencyCode ISO 4217 alphabetic currency code (e.g. USD, EUR, JPY)
 */
data class MonetaryAmountDocument(
    val amount: BigDecimal,
    val currencyCode: String
)