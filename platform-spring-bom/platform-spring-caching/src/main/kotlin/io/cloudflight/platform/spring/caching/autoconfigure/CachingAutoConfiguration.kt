package io.cloudflight.platform.spring.caching.autoconfigure

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.platform.spring.caching.serializer.SafeRedisSessionSerializer
import io.cloudflight.platform.spring.json.ObjectMapperFactory
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.cache.annotation.CachingConfigurerSupport
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
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession

@Configuration
@AutoConfigureBefore(RedisAutoConfiguration::class)
@EnableCaching
@Import(CachingAutoConfiguration.RedisConfiguration::class, CachingAutoConfiguration.RedisSessionConfig::class)
class CachingAutoConfiguration {
    /**
     * Same condition as in RedisAutoConfiguration
     */
    @Configuration
    @ConditionalOnClass(RedisOperations::class)
    class RedisConfiguration : CachingConfigurerSupport() {
        @Bean
        override fun errorHandler(): CacheErrorHandler {
            return io.cloudflight.platform.spring.caching.RedisCacheErrorHandler()
        }

        @Bean
        fun cacheConfiguration(): RedisCacheConfiguration {
            return RedisCacheConfiguration
                .defaultCacheConfig().disableCachingNullValues()
                .serializeKeysWith(fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(fromSerializer(JSON))
        }

        @Bean
        @Lazy
        fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<Any, Any> {
            return RedisTemplate<Any, Any>().apply {
                setConnectionFactory(connectionFactory)
                setDefaultSerializer(JSON)

                keySerializer = RedisSerializer.string()
                hashKeySerializer = RedisSerializer.string()

                valueSerializer = JSON
                hashValueSerializer = JSON

                afterPropertiesSet()
            }
        }
    }

    @Configuration
    @ConditionalOnProperty(value = ["spring.session.store-type"], havingValue = "redis")
    @EnableRedisHttpSession
    class RedisSessionConfig {
        @Bean
        fun springSessionDefaultRedisSerializer() {
            SafeRedisSessionSerializer(RedisSerializer.java())
        }
    }

    companion object {
        private val JSON = createJsonSerializer()

        private fun createJsonSerializer(): GenericJackson2JsonRedisSerializer {
            val objectMapper = ObjectMapperFactory
                .createObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

            objectMapper.activateDefaultTyping(
                objectMapper.polymorphicTypeValidator,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
            )

            return GenericJackson2JsonRedisSerializer(objectMapper)
        }
    }
}
