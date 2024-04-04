package io.cloudflight.platform.spring.caching

import io.cloudflight.platform.spring.caching.autoconfigure.CachingAutoConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.cache.CacheProperties
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.redis.cache.FixedDurationTtlFunction
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration


class CachingAutoConfigurationTest {

    private val contextRunner = ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(CachingAutoConfiguration::class.java))

    @Test
    fun testRedisTypeInfoSerialization() {
        val serializer = CachingAutoConfiguration.createJsonSerializer()

        val data = DataDto("foo")
        val bytes = serializer.serialize(data)
        val obj = serializer.deserialize(bytes, java.lang.Object::class.java) as DataDto

        assertThat(obj.foo).isEqualTo(data.foo)
    }

    private data class DataDto(var foo: String)

    @Test
    fun redisTemplateBackOff() {
        this.contextRunner.withUserConfiguration(CustomRedisTemplateConfiguration::class.java)
            .run { context ->
                assertThat(context).hasSingleBean(RedisTemplate::class.java)
                assertThat(context).getBean("myCustomRedisTemplate")
                    .isSameAs(context.getBean(RedisTemplate::class.java))
            }
    }

    @Test
    fun redisCacheConfigurationWithCacheProperties() {
        this.contextRunner.withUserConfiguration(CustomCachePropertiesConfiguration::class.java)
            .run { context ->
                assertThat(context).hasSingleBean(RedisCacheConfiguration::class.java)
                val redisCacheConfiguration = context.getBean(RedisCacheConfiguration::class.java)
                assertThat(redisCacheConfiguration).extracting(RedisCacheConfiguration::getTtlFunction)
                    .isInstanceOf(FixedDurationTtlFunction::class.java)
                    .extracting("duration")
                    .isEqualTo(Duration.ofSeconds(15))
                assertThat(redisCacheConfiguration.allowCacheNullValues).isFalse()
                assertThat(redisCacheConfiguration.usePrefix()).isTrue()
                assertThat(redisCacheConfiguration.getKeyPrefixFor("")).isEqualTo("foo::")
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
    private class CustomRedisTemplateConfiguration {
        @Bean
        fun cacheProperties(): CacheProperties {
            return CacheProperties()
        }

        @Bean
        fun myCustomRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<*, *>? {
            return mock(RedisTemplate::class.java)
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Import(BasicRedisConfiguration::class)
    private class CustomCachePropertiesConfiguration {
        @Bean
        fun cacheProperties(): CacheProperties {
            return CacheProperties().apply {
                redis.timeToLive = Duration.ofSeconds(15)
                redis.keyPrefix = "foo"
            }
        }
    }
}
