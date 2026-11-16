package io.cloudflight.platform.spring.caching.autoconfigure

import com.fasterxml.jackson.annotation.JsonInclude
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.cache.autoconfigure.CacheProperties
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Lazy
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import tools.jackson.module.kotlin.kotlinModule
import java.time.Duration

@AutoConfiguration(before = [DataRedisAutoConfiguration::class])
@EnableCaching(order = 1000)
@Import(CachingAutoConfiguration.RedisConfiguration::class)
class CachingAutoConfiguration {
    @Configuration
    @ConditionalOnClass(RedisOperations::class)
    class RedisConfiguration {

        @Bean
        fun cacheConfiguration(cacheProperties: CacheProperties): RedisCacheConfiguration {
            var config = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(fromSerializer(JSON))
            val timeToLive = cacheProperties.redis.timeToLive
            if (timeToLive == null || timeToLive == Duration.ZERO) {
                LOG.warn(
                    "No default time-to-live is set for the Redis cache. This can lead to entries remaining" +
                            " forever in the cache. Better set spring.cache.redis.time-to-live as a default value for all " +
                            "caches which you may later override for individual caches."
                )
            } else {
                config = config.entryTtl(timeToLive)
            }
            cacheProperties.redis.keyPrefix?.let { keyPrefix ->
                config = config.prefixCacheNameWith(keyPrefix)
            }
            if (!cacheProperties.redis.isUseKeyPrefix) {
                config = config.disableKeyPrefix()
            }
            return config
        }

        @Bean
        fun platformRedisCacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer {
            return RedisCacheManagerBuilderCustomizer { builder ->
                builder.transactionAware()
            }
        }

        @Bean
        @ConditionalOnMissingBean(RedisTemplate::class)
        @Lazy
        fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<Any, Any> {
            return RedisTemplate<Any, Any>().apply {
                setConnectionFactory(connectionFactory)
                defaultSerializer = JSON

                keySerializer = RedisSerializer.string()
                hashKeySerializer = RedisSerializer.string()

                valueSerializer = JSON
                hashValueSerializer = JSON

                afterPropertiesSet()
            }
        }
    }

    companion object {
        private val JSON = createJsonSerializer()
        private val LOG = KotlinLogging.logger { }

        internal fun createJsonSerializer(): GenericJacksonJsonRedisSerializer {
            // the cache only ever holds data written by the application itself, so a permissive
            // type validator (matching the pre-Jackson-3 behaviour) is acceptable here
            val typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Any::class.java)
                .allowIfSubType { _, _ -> true }
                .build()

            // enableDefaultTyping installs Spring Data Redis' Kotlin-aware type resolver, which
            // types everything except natural types — the equivalent of the Jackson 2
            // DefaultTyping.EVERYTHING setup used before
            return GenericJacksonJsonRedisSerializer.builder()
                .enableSpringCacheNullValueSupport()
                .enableDefaultTyping(typeValidator)
                .customize { mapperBuilder ->
                    mapperBuilder
                        .addModule(kotlinModule())
                        .changeDefaultPropertyInclusion {
                            JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, JsonInclude.Include.USE_DEFAULTS)
                        }
                }
                .build()
        }
    }
}
