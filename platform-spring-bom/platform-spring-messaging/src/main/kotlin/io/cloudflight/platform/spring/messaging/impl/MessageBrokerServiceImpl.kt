package io.cloudflight.platform.spring.messaging.impl

import io.cloudflight.platform.spring.messaging.Message
import io.cloudflight.platform.spring.messaging.MessageBrokerService
import mu.KotlinLogging
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

internal class MessageBrokerServiceImpl(private val rabbitTemplate: RabbitTemplate) : MessageBrokerService {

    override fun sendAfterTransactionCommit(message: Message) {
        sendAfterTransactionCommit(message.queueName, message)
    }

    override fun sendAfterTransactionCommit(queue: String, message: Any) {
        TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
            override fun afterCompletion(status: Int) {
                if (status == TransactionSynchronization.STATUS_COMMITTED) {
                    LOG.debug("Sending message $message to queue $queue")
                    rabbitTemplate.convertAndSend(queue, message)
                } else {
                    LOG.error("Completion state is $status, message $message to queue $queue is not sent")
                }
            }
        })
    }

    companion object {
        private val LOG = KotlinLogging.logger { }
    }
}