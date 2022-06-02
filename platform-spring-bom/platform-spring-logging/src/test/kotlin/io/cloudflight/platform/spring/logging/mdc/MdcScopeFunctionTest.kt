package io.cloudflight.platform.spring.logging.mdc

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.MDC

class MdcScopeFunctionTest {
    private val realMDC = MDC.getMDCAdapter()
    private val key = "normalKey"
    private val value = "normalValue"

    @BeforeEach
    @AfterEach
    fun `MDC is empty`() {
        Assertions.assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty()
    }

    @Test
    fun `MDC can be set inside scope`() {
        val pair = "pairKey" to "pairValue"
        val expected = 10

        val actual = mdcScope {
            MDC.put(key, value)
            MDC.put(pair)

            Assertions.assertThat(realMDC.get(key)).isEqualTo(value)
            Assertions.assertThat(realMDC.get(pair.first)).isEqualTo(pair.second)
            expected
        }

        Assertions.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `MDC can be removed inside scope`() = mdcScope {
        MDC.put(key to value)

        Assertions.assertThat(realMDC.get(key)).isEqualTo(value)

        MDC.remove(key)

        Assertions.assertThat(realMDC.get(key)).isNull()
    }

    @Test
    fun `MDC gets cleaned up when exception is thrown`() {
        assertThrows<IllegalArgumentException> {
            mdcScope {
                MDC.put(key to value)
                throw IllegalArgumentException()
            }
        }
    }
}
