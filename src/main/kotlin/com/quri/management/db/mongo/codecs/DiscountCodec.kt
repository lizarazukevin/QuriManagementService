package com.quri.management.db.mongo.codecs

import com.quri.client.model.Discount
import com.quri.client.model.DiscountType
import com.quri.client.model.MonetaryAmount
import org.bson.BsonReader
import org.bson.BsonType
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.types.Decimal128
import java.math.BigDecimal

class DiscountCodec(private val monetaryAmountCodec: MonetaryAmountCodec) : Codec<Discount> {

    override fun getEncoderClass(): Class<Discount> = Discount::class.java

    override fun encode(
        writer: BsonWriter,
        value: Discount,
        encoderContext: EncoderContext,
    ) {
        writer.writeStartDocument()
        writer.writeString("type", value.type.value)
        value.value?.let { ma ->
            writer.writeName("value")
            monetaryAmountCodec.encode(writer, ma, encoderContext)
        }
        value.rate?.let { writer.writeDecimal128("rate", Decimal128(value.rate)) }
        writer.writeEndDocument()
    }

    override fun decode(
        reader: BsonReader,
        decoderContext: DecoderContext,
    ): Discount {
        var type: DiscountType? = null
        var value: MonetaryAmount? = null
        var rate: BigDecimal? = null

        reader.readStartDocument()
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            when (reader.readName()) {
                "type" -> type = DiscountType.from(reader.readString())
                "value" -> value = monetaryAmountCodec.decode(reader, decoderContext)
                "rate" -> rate = reader.readDecimal128().bigDecimalValue()
            }
        }
        reader.readEndDocument()

        return Discount.builder()
            .typeMember(type!!)
            .value(value)
            .rate(rate)
            .build()
    }
}
