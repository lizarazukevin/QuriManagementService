package com.quri.management.db.mongo.codecs

import com.mongodb.MongoClientSettings
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configures custom MongoDB codecs for domain model serialization.
 *
 * MongoDB's default codec registry doesn't know how to serialize/deserialize the
 * Smithy-generated model types. This configuration registers custom codecs that
 * handle the conversion between BSON documents and these domain objects, streamlining
 * the persistence of complex nested structures.
 */
@Configuration
class MongoCodecConfig {

    @Bean
    fun customCodecRegistry(): CodecRegistry {
        val monetaryAmountCodec = MonetaryAmountCodec()
        val liableCodec = LiableCodec()
        val discountCodec = DiscountCodec(monetaryAmountCodec)

        return CodecRegistries.fromRegistries(
            CodecRegistries.fromCodecs(
                AddressCodec(),
                discountCodec,
                FeeCodec(monetaryAmountCodec),
                ItemCodec(monetaryAmountCodec, liableCodec, discountCodec),
                monetaryAmountCodec,
                UserLocationCodec(),
            ),
            MongoClientSettings.getDefaultCodecRegistry(),
        )
    }
}
