package io.cloudflight.platform.spring.scheduling.noop

import io.cloudflight.platform.spring.context.ApplicationContextProfiles.TEST
import io.cloudflight.platform.spring.scheduling.lock.NoopLockProvider
import net.javacrumbs.shedlock.core.LockProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles(value = [TEST])
@SpringBootTest
class ApplicationTest(
    @Autowired private val lockProvider: LockProvider
) {

    @Test
    fun `context starts`() {
        assertThat(lockProvider).isInstanceOf(NoopLockProvider::class.java)
    }
}
