package com.quri.management.db.mongo.codecs

import com.quri.client.model.MonetaryAmount
import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.types.Decimal128

class MonetaryAmountCodec : Codec<MonetaryAmount> {

    override fun getEncoderClass(): Class<MonetaryAmount> = MonetaryAmount::class.java

    override fun encode(
        writer: BsonWriter,
        value: MonetaryAmount,
        encoderContext: EncoderContext,
    ) {
        writer.writeStartDocument()
        writer.writeDecimal128("amount", Decimal128(value.amount))
        writer.writeString("currency", value.currency)
        writer.writeEndDocument()
    }

    override fun decode(
        reader: BsonReader,
        decoderContext: DecoderContext,
    ): MonetaryAmount {
        reader.readStartDocument()

        val amount = reader.readDecimal128("amount").bigDecimalValue()
        val currency = reader.readString("currency")

        reader.readEndDocument()

        return MonetaryAmount.builder()
            .amount(amount)
            .currency(currency)
            .build()
    }
}
