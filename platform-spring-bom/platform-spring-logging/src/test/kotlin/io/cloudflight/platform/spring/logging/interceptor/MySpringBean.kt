package io.cloudflight.platform.spring.logging.interceptor

import io.cloudflight.platform.spring.logging.annotation.LogParam
import org.assertj.core.api.Assertions.assertThat
import org.slf4j.MDC
import org.springframework.stereotype.Service

@Service
class MySpringBean {

    fun sayHello(@LogParam name: String) {
        assertThat(MDC.get("name")).isEqualTo(name)
    }

    fun sayHelloWithNamedParameter(@LogParam(name = "myName") name: String) {
        assertThat(MDC.get("myName")).isEqualTo(name)
    }

    fun sayHelloWithField(@LogParam(field = "firstName") person: Person) {
        assertThat(MDC.get("person.firstName")).isEqualTo(person.firstName)
    }

    fun sayHelloWithFieldAndName(@LogParam(field = "firstName", name = "myFirstName") person: Person) {
        assertThat(MDC.get("myFirstName")).isEqualTo(person.firstName)
    }

    fun sayHelloWithMultipleFieldNames(
        @io.cloudflight.platform.spring.logging.annotation.LogParams(
            LogParam(field = "firstName"),
            LogParam(field = "lastName")
        ) person: Person
    ) {
        assertThat(MDC.get("person.firstName")).isEqualTo(person.firstName)
        assertThat(MDC.get("person.lastName")).isEqualTo(person.lastName)
    }

    data class Person(val firstName: String, val lastName: String)
}
