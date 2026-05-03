package com.quri.management.shared.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.Address as SmithyAddress

/**
 * Maps to the transaction's address.
 *
 * Currently compatible with US addresses, future work to expand
 * this beyond the constraints of our postal service.
 *
 * @see SmithyAddress
 */
data class Address(
    @JsonProperty("streetAddress") val streetAddress: String,
    @JsonProperty("city") val city: String,
    @JsonProperty("state") val state: String,
    @JsonProperty("postalCode") val postalCode: String,
    @JsonProperty("country") val country: String,

    @JsonProperty("unit") val unit: String? = null,
    @JsonProperty("rawInput") val rawInput: String? = null,
    @JsonProperty("formatted") val formatted: String? = null,
) {
    init {
        require(streetAddress.isNotBlank()) { "streetAddress must not be blank" }
        require(city.isNotBlank()) { "city must not be blank" }
        require(state.isNotBlank()) { "state must not be blank" }
        require(postalCode.isNotBlank()) { "postalCode must not be blank" }
        require(country.matches(Regex("^[A-Z]{2}$"))) { "country must be a valid ISO 3166-1 alpha-2 code e.g. US" }
    }

    fun toSmithyModel(): SmithyAddress =
        SmithyAddress.builder()
            .streetAddress(streetAddress)
            .city(city)
            .state(state)
            .postalCode(postalCode)
            .country(country)
            .unit(unit)
            .rawInput(rawInput)
            .formatted(formatted)
            .build()

    companion object {
        fun from(model: SmithyAddress) =
            Address(
                streetAddress = model.streetAddress,
                city = model.city,
                state = model.state,
                postalCode = model.postalCode,
                country = model.country,
                unit = model.unit,
                rawInput = model.rawInput,
                formatted = model.formatted,
            )
    }
}
