package io.cloudflight.platform.spring.logging.interceptor

import org.assertj.core.api.Assertions
import org.slf4j.MDC
import org.springframework.stereotype.Service

@Service
class BeanImplementation : BeanInterface {

    override fun sayHello(@io.cloudflight.platform.spring.logging.annotation.LogParam name: String) {
        Assertions.assertThat(MDC.get("name")).isEqualTo(name)
    }

    override fun sayHelloWithNamedParameter(@io.cloudflight.platform.spring.logging.annotation.LogParam(name = "myName") name: String) {
        Assertions.assertThat(MDC.get("myName")).isEqualTo(name)
    }
}
