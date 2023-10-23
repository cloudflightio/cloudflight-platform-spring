package io.cloudflight.platform.spring.scheduling.redis

import io.cloudflight.platform.spring.context.ApplicationContextProfiles.TEST
import io.cloudflight.platform.spring.test.testcontainers.redis.RedisContainer
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@ActiveProfiles(value = [TEST])
@SpringBootTest
@Testcontainers
class ApplicationTest(
    @Autowired private val lockProvider: LockProvider
) {

    companion object {
        @ServiceConnection
        @Container
        val redis = RedisContainer()
    }


    @Test
    fun `context starts`() {
        assertThat(lockProvider).isInstanceOf(RedisLockProvider::class.java)
    }
}
