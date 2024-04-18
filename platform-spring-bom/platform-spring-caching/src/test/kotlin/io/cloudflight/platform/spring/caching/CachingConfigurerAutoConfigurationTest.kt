package io.cloudflight.platform.spring.caching

import io.cloudflight.platform.spring.caching.autoconfigure.CachingConfigurerAutoConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.CacheErrorHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory


class CachingConfigurerAutoConfigurationTest {

    private val contextRunner = ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(CachingConfigurerAutoConfiguration::class.java))

    @Test
    fun cacheErrorHandlerBean() {
        this.contextRunner.withUserConfiguration(BasicRedisConfiguration::class.java)
            .run { context ->
                assertThat(context).hasSingleBean(CacheErrorHandler::class.java)
                assertThat(context).getBean("errorHandler")
                    .isSameAs(context.getBean(CacheErrorHandler::class.java))
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
}
