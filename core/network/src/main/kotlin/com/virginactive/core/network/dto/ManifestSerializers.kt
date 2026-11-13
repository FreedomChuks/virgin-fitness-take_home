package com.virginactive.core.network.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

internal abstract class TypeDiscriminatedSerializer<T : Any>(name: String, private val unknown: T) :
    KSerializer<T> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(name)

    protected abstract fun Json.decode(type: String?, element: JsonElement): T

    override fun deserialize(decoder: Decoder): T {
        val input = decoder as? JsonDecoder ?: throw SerializationException("JSON required")
        val element = input.decodeJsonElement()
        val type = ((element as? JsonObject)?.get("type") as? JsonPrimitive)?.contentOrNull
        return try {
            input.json.decode(type, element)
        } catch (_: SerializationException) {
            unknown
        }
    }

    override fun serialize(encoder: Encoder, value: T): Unit =
        throw SerializationException("${descriptor.serialName} is response-only")
}

internal object HomeBlockSerializer :
    TypeDiscriminatedSerializer<HomeBlockDto>("HomeBlockDto", HomeBlockDto.Unknown) {
    override fun Json.decode(type: String?, element: JsonElement): HomeBlockDto =
        when (type) {
            "greeting" -> decodeFromJsonElement<HomeBlockDto.Greeting>(element)
            "hero" -> decodeFromJsonElement<HomeBlockDto.Hero>(element)
            "myClub" -> decodeFromJsonElement<HomeBlockDto.MyClub>(element)
            "classCarousel",
            "myRewards",
            "myGoals" -> decodeFromJsonElement<HomeBlockDto.Tiles>(element)
            "promotion" -> decodeFromJsonElement<HomeBlockDto.Promotion>(element)
            else -> HomeBlockDto.Unknown
        }
}

internal object HomeActionSerializer :
    TypeDiscriminatedSerializer<HomeActionDto>("HomeActionDto", HomeActionDto.Unknown) {
    override fun Json.decode(type: String?, element: JsonElement): HomeActionDto =
        when (type) {
            "openClass" -> decodeFromJsonElement<HomeActionDto.OpenClass>(element)
            "openTimetable" -> decodeFromJsonElement<HomeActionDto.OpenTimetable>(element)
            else -> HomeActionDto.Unknown
        }
}
