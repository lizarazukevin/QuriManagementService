package com.quri.management.api.serialization

import com.quri.client.model.Address
import com.quri.client.model.BillStatus
import com.quri.client.model.CreateBillInput
import com.quri.client.model.CreateProfileInput
import com.quri.client.model.CreateReceiptInput
import com.quri.client.model.Discount
import com.quri.client.model.DiscountType
import com.quri.client.model.Fee
import com.quri.client.model.Gender
import com.quri.client.model.Item
import com.quri.client.model.Liable
import com.quri.client.model.MonetaryAmount
import com.quri.client.model.PaymentMethod
import com.quri.client.model.ProfileLocation
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonPOJOBuilder
import tools.jackson.databind.deser.std.StdDeserializer
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.module.SimpleModule
import tools.jackson.databind.ser.std.StdSerializer

/**
 * Configures Jackson to serialize and deserialize Smithy-generated types,
 * eliminating the need for manual request/response DTO mappings.
 *
 * Responsibilities:
 * - Registers custom serializer/deserializer for Smithy enum types
 * - Registers mixins so Jackson uses Smithy Builders when deserializing input types
 * - Spring Boot auto-discovers the [SimpleModule] and [JsonMapperBuilderCustomizer] beans
 */
@Configuration
class SmithyJacksonConfig {

    /**
     * Registers serializers and deserializers for Smithy value types that aren't standard
     * E.g. Enums with a string value, etc.
     *
     * Spring Boot automatically registers any [Module] bean with its [JsonMapper].
     */
    @Bean
    fun smithyModule(): SimpleModule =
        SimpleModule().apply {
            addSerializer(BillStatus::class.java, BillStatusSerializer())
            addDeserializer(BillStatus::class.java, BillStatusDeserializer())

            addSerializer(PaymentMethod::class.java, PaymentMethodSerializer())
            addDeserializer(PaymentMethod::class.java, PaymentMethodDeserializer())

            addSerializer(DiscountType::class.java, DiscountTypeSerializer())
            addDeserializer(DiscountType::class.java, DiscountTypeDeserializer())

            addSerializer(Gender::class.java, GenderSerializer())
            addDeserializer(Gender::class.java, GenderDeserializer())
        }

    /**
     * Adds Jackson mixins to the autoconfigured [JsonMapper] so that Smithy
     * input types (annotated with [JsonDeserialize]) are constructed via their
     * generated Builder classes, and Builder setters are recognized without
     * a "with" prefix.
     *
     * Sole configuration must be applied to mapper itself as it teaches Jackson
     * how to use the Smithy Builders.
     */
    @Bean
    fun smithyMixinCustomizer(): JsonMapperBuilderCustomizer =
        JsonMapperBuilderCustomizer { builder ->
            builder.addMixIn(CreateBillInput::class.java, CreateBillInputMixin::class.java)
            builder.addMixIn(CreateBillInput.Builder::class.java, CreateBillInputBuilderMixin::class.java)

            builder.addMixIn(CreateProfileInput::class.java, CreateProfileInputMixin::class.java)
            builder.addMixIn(CreateProfileInput.Builder::class.java, CreateProfileInputBuilderMixin::class.java)

            builder.addMixIn(CreateReceiptInput::class.java, CreateReceiptInputMixin::class.java)
            builder.addMixIn(CreateReceiptInput.Builder::class.java, CreateReceiptInputBuilderMixin::class.java)

            builder.addMixIn(Address::class.java, AddressMixin::class.java)
            builder.addMixIn(Address.Builder::class.java, AddressBuilderMixin::class.java)

            builder.addMixIn(Discount::class.java, DiscountMixin::class.java)
            builder.addMixIn(Discount.Builder::class.java, DiscountBuilderMixin::class.java)

            builder.addMixIn(Fee::class.java, FeeMixin::class.java)
            builder.addMixIn(Fee.Builder::class.java, FeeBuilderMixin::class.java)

            builder.addMixIn(Item::class.java, ItemMixin::class.java)
            builder.addMixIn(Item.Builder::class.java, ItemBuilderMixin::class.java)

            builder.addMixIn(Liable::class.java, LiableMixin::class.java)
            builder.addMixIn(Liable.Builder::class.java, LiableBuilderMixin::class.java)

            builder.addMixIn(MonetaryAmount::class.java, MonetaryAmountMixin::class.java)
            builder.addMixIn(MonetaryAmount.Builder::class.java, MonetaryAmountBuilderMixin::class.java)

            builder.addMixIn(ProfileLocation::class.java, ProfileLocationMixin::class.java)
            builder.addMixIn(ProfileLocation.Builder::class.java, ProfileLocationBuilderMixin::class.java)
        }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Serializers / Deserializers
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Serializes a [BillStatus] enum as its Smithy string value (e.g. "DRAFT").
 */
class BillStatusSerializer : StdSerializer<BillStatus>(BillStatus::class.java) {
    override fun serialize(
        value: BillStatus,
        gen: JsonGenerator,
        ctxt: SerializationContext,
    ) {
        gen.writeString(
            value.value,
        )
    }
}

/**
 * Deserializes a [BillStatus] enum from its Smithy string value.
 */
class BillStatusDeserializer : StdDeserializer<BillStatus>(BillStatus::class.java) {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext,
    ): BillStatus = BillStatus.from(p.valueAsString)
}

/**
 * Serializes a [PaymentMethod] enum as its Smithy string value (e.g. "DEBIT").
 */
class PaymentMethodSerializer : StdSerializer<PaymentMethod>(PaymentMethod::class.java) {
    override fun serialize(
        value: PaymentMethod,
        gen: JsonGenerator,
        ctxt: SerializationContext,
    ) {
        gen.writeString(
            value.value,
        )
    }
}

/**
 * Deserializes a [PaymentMethod] enum from its Smithy string value.
 */
class PaymentMethodDeserializer : StdDeserializer<PaymentMethod>(PaymentMethod::class.java) {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext,
    ): PaymentMethod =
        PaymentMethod.from(
            p.valueAsString,
        )
}

/**
 * Serializes a [DiscountType] enum as its Smithy string value (e.g. "COUPON").
 */
class DiscountTypeSerializer : StdSerializer<DiscountType>(DiscountType::class.java) {
    override fun serialize(
        value: DiscountType,
        gen: JsonGenerator,
        ctxt: SerializationContext,
    ) {
        gen.writeString(
            value.value,
        )
    }
}

/**
 * Deserializes a [DiscountType] enum from its Smithy string value.
 */
class DiscountTypeDeserializer : StdDeserializer<DiscountType>(DiscountType::class.java) {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext,
    ): DiscountType =
        DiscountType.from(
            p.valueAsString,
        )
}

/**
 * Serializes a [Gender] enum as its Smithy string value (e.g. "MALE", "FEMALE").
 */
class GenderSerializer : StdSerializer<Gender>(Gender::class.java) {
    override fun serialize(
        value: Gender,
        gen: JsonGenerator,
        ctxt: SerializationContext,
    ) {
        gen.writeString(
            value.value,
        )
    }
}

/**
 * Deserializes a [Gender] enum from its Smithy string value.
 */
class GenderDeserializer : StdDeserializer<Gender>(Gender::class.java) {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext,
    ): Gender = Gender.from(p.valueAsString)
}

// ═══════════════════════════════════════════════════════════════════════════════
// Mixin classes — never instantiated
// `Inputs` - request bodies needing to be serialized to a Smithy model
// `Nested` - structured JSON types embedded in a Smith model
// ═══════════════════════════════════════════════════════════════════════════════

// Inputs
@JsonDeserialize(builder = CreateBillInput.Builder::class)
abstract class CreateBillInputMixin

@JsonDeserialize(builder = CreateProfileInput.Builder::class)
abstract class CreateProfileInputMixin

@JsonDeserialize(builder = CreateReceiptInput.Builder::class)
abstract class CreateReceiptInputMixin

@JsonPOJOBuilder(withPrefix = "")
abstract class CreateProfileInputBuilderMixin

@JsonPOJOBuilder(withPrefix = "")
abstract class CreateBillInputBuilderMixin

@JsonPOJOBuilder(withPrefix = "")
abstract class CreateReceiptInputBuilderMixin

// Nested
@JsonDeserialize(builder = Address.Builder::class)
abstract class AddressMixin

@JsonDeserialize(builder = Discount.Builder::class)
abstract class DiscountMixin

@JsonDeserialize(builder = Fee.Builder::class)
abstract class FeeMixin

@JsonDeserialize(builder = Item.Builder::class)
abstract class ItemMixin

@JsonDeserialize(builder = Liable.Builder::class)
abstract class LiableMixin

@JsonDeserialize(builder = MonetaryAmount.Builder::class)
abstract class MonetaryAmountMixin

@JsonDeserialize(builder = ProfileLocation.Builder::class)
abstract class ProfileLocationMixin

@JsonPOJOBuilder(withPrefix = "")
abstract class AddressBuilderMixin

@JsonPOJOBuilder(withPrefix = "")
abstract class DiscountBuilderMixin

@JsonPOJOBuilder(withPrefix = "")
abstract class FeeBuilderMixin

@JsonPOJOBuilder(withPrefix = "")
abstract class ItemBuilderMixin

@JsonPOJOBuilder(withPrefix = "")
abstract class LiableBuilderMixin

@JsonPOJOBuilder(withPrefix = "")
abstract class MonetaryAmountBuilderMixin

@JsonPOJOBuilder(withPrefix = "")
abstract class ProfileLocationBuilderMixin
