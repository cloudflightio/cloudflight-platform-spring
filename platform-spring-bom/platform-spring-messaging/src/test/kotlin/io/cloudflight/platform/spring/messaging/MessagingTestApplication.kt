package io.cloudflight.platform.spring.messaging

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@SpringBootApplication
class MessagingTestApplication

@Configuration
class MessagingConfiguration {

    @Bean
    fun myQueue(): Queue {
        return Queue("myQueue")
    }
}

data class MyMessage(val value1: String, val value2: String) : Message {
    override val queueName: String
        get() = "myQueue"
}

interface TransactionalService {
    fun performAction(value1: String, value2: String)

    fun lastActionPerformed(): Long?
}

@Service
class TransactionalServiceImpl(private val messageBrokerService: MessageBrokerService) : TransactionalService {

    private var lastActionPerformed: Long? = null

    @Transactional
    override fun performAction(value1: String, value2: String) {
        lastActionPerformed = System.currentTimeMillis()
        Thread.sleep(100)
        messageBrokerService.sendAfterTransactionCommit(MyMessage(value1, value2))
        if (value2 == "throw") {
            throw IllegalArgumentException("something happened")
        }
    }

    override fun lastActionPerformed(): Long? {
        return lastActionPerformed
    }
}

@Component
class MyListener {

    var lastMessageReceivedThreadName: String? = null
    var lastMessageReceived: MyMessage? = null
    var lastMessageReceivedTime: Long? = null

    @RabbitListener(id = "myListener", queues = ["myQueue"])
    fun messageReceived(message: MyMessage) {
        LOG.info("Received $message")
        lastMessageReceived = message
        lastMessageReceivedTime = System.currentTimeMillis()
        lastMessageReceivedThreadName = Thread.currentThread().name
    }

    companion object {
        val LOG = LoggerFactory.getLogger(MyListener::class.java)
    }
}
