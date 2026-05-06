package com.quri.management.db.mongo.codecs

import com.quri.client.model.Address
import org.bson.BsonReader
import org.bson.BsonType
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext

class AddressCodec : Codec<Address> {

    override fun getEncoderClass(): Class<Address> = Address::class.java

    override fun encode(
        writer: BsonWriter,
        value: Address,
        encoderContext: EncoderContext,
    ) {
        writer.writeStartDocument()
        writer.writeString("street", value.street)
        writer.writeString("city", value.city)
        writer.writeString("state", value.state)
        writer.writeString("postalCode", value.postalCode)
        writer.writeString("country", value.country)
        value.unit?.let { writer.writeString("unit", value.unit) }
        value.rawInput?.let { writer.writeString("rawInput", value.rawInput) }
        value.formatted?.let { writer.writeString("formatted", value.formatted) }
        writer.writeEndDocument()
    }

    override fun decode(
        reader: BsonReader,
        decoderContext: DecoderContext,
    ): Address {
        var street: String? = null
        var city: String? = null
        var state: String? = null
        var postalCode: String? = null
        var country: String? = null
        var unit: String? = null
        var rawInput: String? = null
        var formatted: String? = null

        reader.readStartDocument()
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            when (reader.readName()) {
                "street" -> street = reader.readString()
                "city" -> city = reader.readString()
                "state" -> state = reader.readString()
                "postalCode" -> postalCode = reader.readString()
                "country" -> country = reader.readString()
                "unit" -> unit = reader.readString()
                "rawInput" -> rawInput = reader.readString()
                "formatted" -> formatted = reader.readString()
            }
        }
        reader.readEndDocument()

        return Address.builder()
            .street(street!!)
            .city(city!!)
            .state(state!!)
            .postalCode(postalCode!!)
            .country(country!!)
            .unit(unit)
            .rawInput(rawInput)
            .formatted(formatted)
            .build()
    }
}
