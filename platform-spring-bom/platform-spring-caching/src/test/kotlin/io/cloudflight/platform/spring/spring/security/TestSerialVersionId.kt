package io.cloudflight.platform.spring.spring.security

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.security.core.SpringSecurityCoreVersion

/**
 * @author Harald Radi (harald.radi@cloudflight.io)
 * @version 1.0
 */
class TestSerialVersionId {
    @Test
    fun ensureSpringSecurityCoreVersion() {
        Assertions.assertEquals(
            560L,
            SpringSecurityCoreVersion.SERIAL_VERSION_UID,
            "if that version changes, then http sessions aren't deserializable anymore. add a note to the " +
                    "changelog and increase the version number accordingly to signal a breaking change."
        )
    }
}
