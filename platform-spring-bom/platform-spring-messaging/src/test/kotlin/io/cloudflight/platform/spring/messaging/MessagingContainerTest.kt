package io.cloudflight.platform.spring.messaging

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Fail.fail
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.ApplicationContext
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration

@SpringBootTest(classes = [MessagingTestApplication::class])
@Testcontainers
class MessagingContainerTest(
    @Autowired private val applicationContext: ApplicationContext,
    @Autowired private val transactionalService: TransactionalService,
    @Autowired private val myListener: MyListener
) {

    companion object {
        @ServiceConnection
        @Container
        val rabbit: RabbitMQContainer = RabbitMQContainer()
    }

    @BeforeEach
    fun resetListener() {
        myListener.lastMessageReceived = null
    }

    @Test
    fun sendMessageAfterTransactionCommit() {
        transactionalService.performAction("foo", "bar")

        await().atMost(Duration.ofSeconds(2)).until {
            myListener.lastMessageReceived != null
        }

        with(myListener) {
            assertThat(lastMessageReceived).isNotNull
            assertThat(lastMessageReceived?.value1).isEqualTo("foo")
            assertThat(lastMessageReceivedTime!!).isGreaterThan(transactionalService.lastActionPerformed()!!)
                .`as`("listener must have been notified afterwards")
            assertThat(lastMessageReceivedThreadName).startsWith("myListener")
        }
    }

    @Test
    fun doNotSendMessageInCaseOfExceptionInsideTransaction() {
        try {
            transactionalService.performAction("foo", "throw")
            fail<String>("method should throw an exception")
        } catch (ex: Exception) {
            // OK
        }

        Thread.sleep(2000)
        assertThat(myListener.lastMessageReceived).isNull()
    }

    @Test
    fun deactivateListener() {
        applicationContext.publishEvent(ProcessControlEvent("rabbit-myListener", false))

        transactionalService.performAction("foo", "bar")

        Thread.sleep(2000)
        assertThat(myListener.lastMessageReceived).`as`("no message must have been received").isNull()
        applicationContext.publishEvent(ProcessControlEvent("rabbit-myListener", true))

        await().atMost(Duration.ofSeconds(2)).until {
            myListener.lastMessageReceived != null
        }
        assertThat(myListener.lastMessageReceived).isNotNull
    }
}
