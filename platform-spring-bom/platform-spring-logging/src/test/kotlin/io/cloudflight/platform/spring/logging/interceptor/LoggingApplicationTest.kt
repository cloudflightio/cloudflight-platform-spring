package io.cloudflight.platform.spring.logging.interceptor

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [LoggingApplication::class])
class LoggingApplicationTest(
    @Autowired private val bean: MySpringBean,
    @Autowired private val bean2: BeanInterface
) {

    @BeforeEach
    @AfterEach
    fun `MDC is empty`() {
        assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty()
    }

    @Test
    fun `LogParam works on a spring bean without interface (cglib proxy)`() {
        bean.sayHelloWithNamedParameter("hallo")
    }

    @Test
    fun `LogParam on implementation of an interface is considered as well`() {
        bean2.sayHelloWithNamedParameter("hello")
    }

    @Test
    fun `field name is being taken cglib proxies`() {
        bean.sayHello("hallo")
    }

    @Test
    fun `field name is being taken aop proxies`() {
        bean2.sayHello("hallo")
    }

    @Test
    fun `field is being evaluated as SPEL`() {
        bean.sayHelloWithField(MySpringBean.Person(firstName = "John", lastName = "Doe"))
    }

    @Test
    fun `field is being evaluated as SPEL, name is overridden`() {
        bean.sayHelloWithFieldAndName(MySpringBean.Person(firstName = "John", lastName = "Doe"))
    }

    @Test
    fun `multiple LogParams can be used as well`() {
        bean.sayHelloWithMultipleFieldNames(MySpringBean.Person(firstName = "John", lastName = "Doe"))
    }

}
