package com.quri.management.db.mongo.codecs

import com.quri.client.model.UserLocation
import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext

class UserLocationCodec : Codec<UserLocation> {

    override fun getEncoderClass(): Class<UserLocation> = UserLocation::class.java

    override fun encode(writer: BsonWriter, value: UserLocation, encoderContext: EncoderContext) {
        writer.writeStartDocument()
        writer.writeString("city", value.city)
        writer.writeString("state", value.state)
        writer.writeString("country", value.country)
        writer.writeEndDocument()
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): UserLocation {
        reader.readStartDocument()

        val city = reader.readString("city")
        val state = reader.readString("state")
        val country = reader.readString("country")

        reader.readEndDocument()

        return UserLocation.builder()
            .city(city)
            .state(state)
            .country(country)
            .build()
    }
}
