package io.cloudflight.platform.spring.server

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PlatformServerModuleStartupListenerTest {

    @Test
    fun startup() {
        val identification: ServerModuleIdentification = PlatformServerModuleStartupListener(null, null, mockk())
        assertThat(identification.getVersion()).isEqualTo("unknown")
    }
}