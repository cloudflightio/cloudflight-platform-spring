package io.cloudflight.platform.spring.caching

import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.cache.interceptor.SimpleCacheErrorHandler
import org.springframework.data.redis.serializer.SerializationException

class EvictCacheErrorHandler : SimpleCacheErrorHandler() {
    override fun handleCacheGetError(exception: RuntimeException, cache: Cache, key: Any) {
        if (exception is SerializationException) {
            LOG.error("SerializationException during (de)serialization of value with key '$key' at cache ${cache.name} " +
                    "with reason ${exception.mostSpecificCause.message}. '$key' is evicted from the cache.")
            cache.evict(key)
        } else {
            throw exception
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(EvictCacheErrorHandler::class.java)
    }
}
