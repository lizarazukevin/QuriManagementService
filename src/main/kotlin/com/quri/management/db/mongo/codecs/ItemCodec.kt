package com.quri.management.db.mongo.codecs

import com.quri.client.model.Discount
import com.quri.client.model.Item
import com.quri.client.model.Liable
import com.quri.client.model.MonetaryAmount
import org.bson.BsonReader
import org.bson.BsonType
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext

class ItemCodec(
    private val monetaryAmountCodec: MonetaryAmountCodec,
    private val liableCodec: LiableCodec,
    private val discountCodec: DiscountCodec,
) : Codec<Item> {

    override fun getEncoderClass(): Class<Item> = Item::class.java

    override fun encode(
        writer: BsonWriter,
        value: Item,
        encoderContext: EncoderContext,
    ) {
        writer.writeStartDocument()
        writer.writeString("name", value.name)
        writer.writeInt32("units", value.units)
        value.unitCost.let { ma ->
            writer.writeName("unitCost")
            monetaryAmountCodec.encode(writer, ma, encoderContext)
        }
        value.liable?.let { list ->
            writer.writeStartArray("liable")
            list.forEach { liable ->
                liableCodec.encode(writer, liable, encoderContext)
            }
            writer.writeEndArray()
        }
        value.discounts?.let { list ->
            writer.writeStartArray("discounts")
            list.forEach { discount ->
                discountCodec.encode(writer, discount, encoderContext)
            }
            writer.writeEndArray()
        }
        writer.writeEndDocument()
    }

    override fun decode(
        reader: BsonReader,
        decoderContext: DecoderContext,
    ): Item {
        var name: String? = null
        var units: Int? = null
        var unitCost: MonetaryAmount? = null
        var liable: List<Liable>? = emptyList()
        var discounts: List<Discount>? = emptyList()

        reader.readStartDocument()
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            when (reader.readName()) {
                "name" -> name = reader.readString()

                "units" -> units = reader.readInt32()

                "unitCost" -> unitCost = monetaryAmountCodec.decode(reader, decoderContext)

                "liable" -> {
                    val list = mutableListOf<Liable>()
                    reader.readStartArray()
                    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                        list.add(liableCodec.decode(reader, decoderContext))
                    }
                    reader.readEndArray()
                    liable = list
                }

                "discounts" -> {
                    val list = mutableListOf<Discount>()
                    reader.readStartArray()
                    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                        list.add(discountCodec.decode(reader, decoderContext))
                    }
                    reader.readEndArray()
                    discounts = list
                }
            }
        }
        reader.readEndDocument()

        return Item.builder()
            .name(name!!)
            .units(units!!)
            .unitCost(unitCost!!)
            .liable(liable)
            .discounts(discounts)
            .build()
    }
}
