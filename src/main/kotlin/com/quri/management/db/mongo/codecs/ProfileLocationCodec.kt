package com.quri.management.db.mongo.codecs

import com.quri.client.model.ProfileLocation
import org.bson.BsonReader
import org.bson.BsonType
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext

class ProfileLocationCodec : Codec<ProfileLocation> {

    override fun getEncoderClass(): Class<ProfileLocation> = ProfileLocation::class.java

    override fun encode(
        writer: BsonWriter,
        value: ProfileLocation,
        encoderContext: EncoderContext,
    ) {
        writer.writeStartDocument()
        writer.writeString("city", value.city)
        writer.writeString("state", value.state)
        writer.writeString("country", value.country)
        writer.writeEndDocument()
    }

    override fun decode(
        reader: BsonReader,
        decoderContext: DecoderContext,
    ): ProfileLocation {
        var city: String? = null
        var state: String? = null
        var country: String? = null

        reader.readStartDocument()
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            when (reader.readName()) {
                "city" -> city = reader.readString()
                "state" -> state = reader.readString()
                "country" -> country = reader.readString()
            }
        }
        reader.readEndDocument()

        return ProfileLocation.builder()
            .city(city!!)
            .state(state!!)
            .country(country!!)
            .build()
    }
}
