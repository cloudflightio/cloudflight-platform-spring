package io.cloudflight.platform.spring.caching.autoconfigure

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.platform.spring.caching.RedisCacheErrorHandler
import io.cloudflight.platform.spring.json.ObjectMapperFactory
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.cache.CacheProperties
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.CacheErrorHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Lazy
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import java.time.Duration

@AutoConfiguration(before = [RedisAutoConfiguration::class])
@EnableCaching(order = 1000)
@Import(CachingAutoConfiguration.RedisConfiguration::class)
class CachingAutoConfiguration {

    @Configuration
    @ConditionalOnClass(RedisOperations::class)
    @ConditionalOnBean(RedisConnectionFactory::class)
    class RedisConfiguration : CachingConfigurer {
        @Bean
        override fun errorHandler(): CacheErrorHandler {
            return RedisCacheErrorHandler()
        }

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
            if (cacheProperties.redis.keyPrefix != null) {
                config = config.prefixCacheNameWith(cacheProperties.redis.keyPrefix)
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

        internal fun createJsonSerializer(): GenericJackson2JsonRedisSerializer {
            val objectMapper = ObjectMapperFactory
                .createObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

            objectMapper.activateDefaultTyping(
                objectMapper.polymorphicTypeValidator,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
            )

            return GenericJackson2JsonRedisSerializer(objectMapper)
        }
    }
}
