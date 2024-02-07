package io.cloudflight.platform.spring.caching.autoconfigure

import io.cloudflight.platform.spring.caching.serializer.SafeRedisSessionSerializer
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.session.Session
import org.springframework.session.SessionRepository
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession

@AutoConfiguration(
    after = [CachingAutoConfiguration::class, RedisAutoConfiguration::class],
    before = [org.springframework.boot.autoconfigure.session.SessionAutoConfiguration::class]
)
@ConditionalOnClass(Session::class)
@ConditionalOnWebApplication
@Import(SessionAutoConfiguration.RedisSessionConfig::class)
class SessionAutoConfiguration {

    @Configuration
    @ConditionalOnClass(RedisOperations::class)
    @ConditionalOnBean(RedisConnectionFactory::class)
    @ConditionalOnMissingBean(SessionRepository::class)
    @EnableRedisHttpSession
    class RedisSessionConfig

    @Bean
    @ConditionalOnClass(RedisOperations::class)
    @ConditionalOnBean(SessionRepository::class)
    @ConditionalOnMissingBean(RedisSerializer::class)
    fun springSessionDefaultRedisSerializer(): RedisSerializer<*> {
        return SafeRedisSessionSerializer(RedisSerializer.java())
    }
}
