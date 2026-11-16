package io.cloudflight.platform.spring.messaging.autoconfigure

import io.cloudflight.platform.spring.messaging.MessageBrokerService
import io.cloudflight.platform.spring.messaging.ProcessControlRegistry
import io.cloudflight.platform.spring.messaging.impl.MessageBrokerServiceImpl
import io.cloudflight.platform.spring.messaging.impl.RabbitListenerControlService
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration
import org.springframework.context.annotation.Bean
import tools.jackson.databind.json.JsonMapper

/**
 * AutoConfiguration which adds support for asynchronous messaging via Spring Boot's AMQP support.
 * This configuration registers a [MessageBrokerService] which servers as gateway for all clients
 * to send messages immediately or after transaction commit.
 *
 * @author Klaus Lehner
 */
@AutoConfiguration(after = [RabbitAutoConfiguration::class])
class PlatformMessagingAutoConfiguration {

    @Autowired(required = false)
    var processControlRegistry: ProcessControlRegistry? = null

    @Bean
    @ConditionalOnBean(value = [RabbitListenerEndpointRegistry::class])
    fun rabbitListenerStarter(registry: RabbitListenerEndpointRegistry): ApplicationRunner {
        return RabbitListenerControlService(registry, processControlRegistry)
    }

    @Bean
    fun producerJacksonMessageConverter(jsonMapper: JsonMapper): JacksonJsonMessageConverter {
        return JacksonJsonMessageConverter(jsonMapper)
    }

    @Bean
    fun messageBrokerService(rabbitTemplate: RabbitTemplate): MessageBrokerService {
        return MessageBrokerServiceImpl(rabbitTemplate)
    }
}