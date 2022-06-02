package io.cloudflight.platform.spring.caching.serializer

import org.slf4j.LoggerFactory
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.SerializationException

/**
 * @author Harald Radi (harald.radi@cloudflight.io)
 * @version 1.0
 */
class SafeRedisSessionSerializer<T>(
    private val serializer: RedisSerializer<T>
) : RedisSerializer<T> {
    override fun serialize(t: T?): ByteArray? {
        return serializer.serialize(t)
    }

    override fun deserialize(bytes: ByteArray?): T? {
        return try {
            serializer.deserialize(bytes)
        } catch (e: SerializationException) {
            LOG.warn("non-deserializable session")
            null
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(SafeRedisSessionSerializer::class.java)
    }
}
