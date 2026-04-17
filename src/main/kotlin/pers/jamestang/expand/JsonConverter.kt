package pers.jamestang.expand

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import tools.jackson.core.JacksonException
import tools.jackson.core.JsonEncoding
import tools.jackson.core.JsonGenerator
import tools.jackson.core.StreamWriteFeature
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.io.Flushable
import kotlin.text.Charsets

class JsonConverter(
    private val objectMapper: ObjectMapper,
    private val streamRequestBody: Boolean = true,
) : ContentConverter {
    override suspend fun serialize(
        contentType: ContentType,
        charset: Charset,
        typeInfo: TypeInfo,
        value: Any?,
    ): OutgoingContent {
        if (!streamRequestBody && typeInfo.type != Flow::class) {
            return TextContent(
                objectMapper.writeValueAsString(value),
                contentType.withCharsetIfNeeded(charset),
            )
        }

        return OutputStreamContent(
            {
                if (charset == Charsets.UTF_8) {
                    val generator = objectMapper.createGenerator(this, JsonEncoding.UTF8)
                    writeValue(typeInfo, value, generator, this)
                } else {
                    val writer = this.writer(charset = charset)
                    val generator = objectMapper.createGenerator(writer)
                    writeValue(typeInfo, value, generator, writer)
                }
            },
            contentType.withCharsetIfNeeded(charset),
        )
    }

    override suspend fun deserialize(
        charset: Charset,
        typeInfo: TypeInfo,
        content: ByteReadChannel,
    ): Any? {
        try {
            val type = objectMapper.constructType(typeInfo.type.javaObjectType)
            return if (isUnicode(charset)) {
                objectMapper.readValue(content.toInputStream(), type)
            } else {
                content.toInputStream().reader(charset).use { reader ->
                    objectMapper.readValue(reader, type)
                }
            }
        } catch (cause: Exception) {
            val convertException = JsonConvertException("Illegal json parameter found: ${cause.message}", cause)
            when (cause) {
                is JacksonException -> throw convertException
                else -> throw cause
            }
        }
    }

    private suspend fun writeValue(
        typeInfo: TypeInfo,
        value: Any?,
        generator: JsonGenerator,
        stream: Flushable,
    ) {
        generator.configure(StreamWriteFeature.FLUSH_PASSED_TO_STREAM, false)

        if (typeInfo.type == Flow::class && value is Flow<*>) {
            generator.writeStartArray()
            value.collect { item ->
                objectMapper.writeValue(generator, item)
                withContext(Dispatchers.IO) {
                    stream.flush()
                }
            }

            generator.writeEndArray()
            generator.flush()
            withContext(Dispatchers.IO) {
                stream.flush()
            }
            return
        }

        objectMapper.writeValue(generator, value)
    }

    private companion object {
        private val jacksonEncodings = buildSet<String> {
            JsonEncoding.entries.forEach { add(it.javaName) }
            add("US-ASCII")
        }

        private fun isUnicode(charset: Charset): Boolean {
            return charset.name() in jacksonEncodings ||
                charset == Charsets.UTF_16 ||
                charset == Charsets.UTF_32
        }
    }
}

fun Configuration.jackson3(
    contentType: ContentType = ContentType.Application.Json,
    streamRequestBody: Boolean = true,
    block: ObjectMapper.() -> Unit = {},
) {
    val mapper = jacksonObjectMapper().apply {
        block()
    }

    register(contentType, JsonConverter(mapper, streamRequestBody))
}