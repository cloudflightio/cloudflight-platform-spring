package io.cloudflight.platform.spring.test.testcontainers.redis

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class RedisContainerTest {

    companion object {
        @Container
        @ServiceConnection("redis")
        val redis = RedisContainer()
    }

    /**
     * This test is just to check if the redis container is started.
     */
    @Test
    fun setValueToRedis() {
        assertThat(redis.isRunning).isTrue
        // create a RedisTemplate and set a value
        val template = RedisTemplate<String, String>()
        template.connectionFactory = LettuceConnectionFactory(redis.host, redis.firstMappedPort).also { it.afterPropertiesSet() }
        template.afterPropertiesSet()

        template.opsForValue().set("foo", "bar")
        val value = template.opsForValue().get("foo")
        assertThat(value).isEqualTo("bar")
    }
}