package io.cloudflight.platform.spring.caching

import io.cloudflight.platform.spring.caching.autoconfigure.SessionAutoConfiguration
import io.cloudflight.platform.spring.caching.serializer.SafeRedisSessionSerializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.FilteredClassLoader
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.session.Session
import org.springframework.session.SessionRepository
import org.springframework.session.data.redis.RedisIndexedSessionRepository
import org.springframework.session.data.redis.RedisSessionRepository

class SessionAutoConfigurationTest {

    private val contextRunner = WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SessionAutoConfiguration::class.java))

    @Test
    fun autoConfigurationDisabledIfNoClassMatches() {
        this.contextRunner.withClassLoader(FilteredClassLoader(Session::class.java))
            .withUserConfiguration(BasicRedisConfiguration::class.java)
            .run { context ->
                assertThat(context).doesNotHaveBean(SessionRepository::class.java)
            }
    }

    @Test
    fun defaultRedisHttpSession() {
        this.contextRunner.withUserConfiguration(BasicRedisConfiguration::class.java)
            .run { context ->
                assertThat(context).hasSingleBean(SessionRepository::class.java)
                assertThat(context).getBean("sessionRepository")
                    .isInstanceOf(RedisSessionRepository::class.java)
            }
    }

    @Test
    fun redisHttpSessionBackOff() {
        this.contextRunner.withUserConfiguration(CustomRedisIndexedHttpSessionConfiguration::class.java)
            .run { context ->
                assertThat(context).hasSingleBean(SessionRepository::class.java)
                assertThat(context).getBean("sessionRepository")
                    .isInstanceOf(RedisIndexedSessionRepository::class.java)
            }
    }

    @Test
    fun safeRedisSessionSerializerBackoff() {
        this.contextRunner.withUserConfiguration(CustomRedisSerializerConfiguration::class.java)
            .run { context ->
                assertThat(context).hasSingleBean(RedisSerializer::class.java)
                assertThat(context).getBean("customRedisSerializer")
                    .isInstanceOf(SafeRedisSessionSerializer::class.java)
            }
    }

    @Test
    fun defaultSafeRedisSessionSerializer() {
        this.contextRunner.withUserConfiguration(CustomRedisIndexedHttpSessionConfiguration::class.java)
            .run { context ->
                assertThat(context).hasSingleBean(RedisSerializer::class.java)
                assertThat(context).getBean("springSessionDefaultRedisSerializer")
                    .isInstanceOf(SafeRedisSessionSerializer::class.java)
            }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableCaching
    private class BasicRedisConfiguration {
        @Bean
        fun redisConnectionFactory(): RedisConnectionFactory {
            return mock(RedisConnectionFactory::class.java)
        }

        @Bean
        fun cacheManager(connectionFactory: RedisConnectionFactory): CacheManager {
            return RedisCacheManager.create(connectionFactory)
        }
    }


    @Configuration(proxyBeanMethods = false)
    @Import(BasicRedisConfiguration::class)
    private class CustomRedisIndexedHttpSessionConfiguration {

        @Bean
        fun redisTemplate(): RedisTemplate<*, *>? {
            return mock(RedisTemplate::class.java)
        }

        @Bean
        fun sessionRepository(): RedisIndexedSessionRepository {
            return mock(RedisIndexedSessionRepository::class.java)
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Import(BasicRedisConfiguration::class)
    private class CustomRedisSerializerConfiguration {

        @Bean
        fun customRedisSerializer(): RedisSerializer<*> {
            return SafeRedisSessionSerializer(RedisSerializer.java())
        }
    }
}
