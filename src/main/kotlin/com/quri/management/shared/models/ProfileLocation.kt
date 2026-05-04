package com.quri.management.shared.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.ProfileLocation as SmithyProfileLocation

/**
 * Maps to a user's general location for demographic data.
 *
 * @see SmithyProfileLocation
 */
data class ProfileLocation(
    @JsonProperty("city") val city: String,
    @JsonProperty("state") val state: String,
    @JsonProperty("country") val country: String,
) {
    fun toSmithyModel(): SmithyProfileLocation =
        SmithyProfileLocation.builder()
            .city(city)
            .state(state)
            .country(country)
            .build()

    companion object {
        fun from(model: SmithyProfileLocation) =
            ProfileLocation(
                city = model.city,
                state = model.state,
                country = model.country,
            )
    }
}
