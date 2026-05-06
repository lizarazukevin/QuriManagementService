package com.quri.management.db.mongo.codecs

import com.quri.client.model.MonetaryAmount
import org.bson.BsonReader
import org.bson.BsonType
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
        var amount: java.math.BigDecimal? = null
        var currency: String? = null

        reader.readStartDocument()
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            when (reader.readName()) {
                "amount" -> amount = reader.readDecimal128().bigDecimalValue()
                "currency" -> currency = reader.readString()
            }
        }
        reader.readEndDocument()

        return MonetaryAmount.builder()
            .amount(amount!!)
            .currency(currency!!)
            .build()
    }
}
