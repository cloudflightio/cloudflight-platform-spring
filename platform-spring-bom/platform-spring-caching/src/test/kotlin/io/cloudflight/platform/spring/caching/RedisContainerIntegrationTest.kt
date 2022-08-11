package io.cloudflight.platform.spring.caching

import io.cloudflight.platform.spring.context.ApplicationContextProfiles
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import java.time.Duration

@SpringBootTest(classes = [CachingTestApplication::class])
@ActiveProfiles(ApplicationContextProfiles.TEST_CONTAINER)
class RedisContainerIntegrationTest(
    @Autowired private val redisTemplate: RedisTemplate<Any, Any>
) {

    @Test
    fun contextStarts() {
        val opsForValue = redisTemplate.opsForValue()
        opsForValue.set("myKey", "myValue", Duration.ofSeconds(10))
        assertThat(opsForValue.get("myKey")).isEqualTo("myValue")
    }
}