package io.cloudflight.platform.spring.messaging

/**
 * Wrapper around [org.springframework.amqp.rabbit.core.RabbitTemplate] to send messages to RabbitMQ
 *
 * @author Klaus Lehner
 */
interface MessageBrokerService {

    /**
     * Sends the given message after transaction commit of the current transaction. This method will raise
     * an exception if we are currently not inside a transaction
     */
    fun sendAfterTransactionCommit(message: Message)

    /**
     * Sends the given message after transaction commit of the current transaction. This method will raise
     * an exception if we are currently not inside a transaction
     */
    fun sendAfterTransactionCommit(queue: String, message: Any)
}