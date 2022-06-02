package io.cloudflight.platform.spring.server

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ServerStartupTest(@Autowired private val serverModuleIdentification: ServerModuleIdentification) {

    @Test
    fun startup() {
        assertThat(serverModuleIdentification.getGroup()).isNotBlank()
    }
}