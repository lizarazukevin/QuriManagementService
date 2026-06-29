package com.quri.management.db.mongo.codecs

import com.quri.client.model.Liable
import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.types.Decimal128

class LiableCodec : Codec<Liable> {

    override fun getEncoderClass(): Class<Liable> = Liable::class.java

    override fun encode(writer: BsonWriter, value: Liable, encoderContext: EncoderContext) {
        writer.writeStartDocument()
        writer.writeString("userId", value.userId)
        writer.writeDecimal128("rate", Decimal128(value.rate))
        writer.writeEndDocument()
    }

    override fun decode(reader: BsonReader, context: DecoderContext): Liable {
        reader.readStartDocument()

        val userId = reader.readString("userId")
        val rate = reader.readDecimal128("rate").bigDecimalValue()

        reader.readEndDocument()

        return Liable.builder()
            .userId(userId)
            .rate(rate)
            .build()
    }
}
