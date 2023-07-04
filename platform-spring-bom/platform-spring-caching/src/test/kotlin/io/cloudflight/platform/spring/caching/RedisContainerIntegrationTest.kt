package io.cloudflight.platform.spring.caching

import io.cloudflight.platform.spring.context.ApplicationContextProfiles
import io.cloudflight.platform.spring.test.testcontainers.redis.RedisContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration

@SpringBootTest(classes = [CachingTestApplication::class])
@ActiveProfiles(ApplicationContextProfiles.TEST_CONTAINER)
@Testcontainers
class RedisContainerIntegrationTest(
    @Autowired private val redisTemplate: RedisTemplate<Any, Any>
) {

    companion object {
        @Container
        @ServiceConnection("redis")
        val redis = RedisContainer()
    }

    @Test
    fun contextStarts() {
        val opsForValue = redisTemplate.opsForValue()
        opsForValue.set("myKey", "myValue", Duration.ofSeconds(10))
        assertThat(opsForValue.get("myKey")).isEqualTo("myValue")
    }
}