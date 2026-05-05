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
        require(
            postalCode.matches(Regex("""^[0-9]{5}(-[0-9]{4})?$""")),
        ) { "postalCode must be a valid US zip code e.g. 20001 or 20001-1234" }
        require(country.matches(Regex("""^[A-Z]{2}$"""))) { "country must be a valid ISO 3166-1 alpha-2 code e.g. US" }

        unit?.let { require(it.isNotBlank()) { "unit must not be blank" } }
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
