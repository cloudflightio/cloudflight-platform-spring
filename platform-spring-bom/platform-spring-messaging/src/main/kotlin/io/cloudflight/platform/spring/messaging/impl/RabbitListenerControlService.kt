package io.cloudflight.platform.spring.messaging.impl

import io.cloudflight.platform.spring.messaging.ProcessControlEvent
import io.cloudflight.platform.spring.messaging.ProcessControlRegistry
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationListener
import org.springframework.core.task.SimpleAsyncTaskExecutor

internal class RabbitListenerControlService(
        private val registry: RabbitListenerEndpointRegistry,
        private val processControlRegistry: ProcessControlRegistry?
) : ApplicationRunner, ApplicationListener<ProcessControlEvent> {

    override fun run(args: ApplicationArguments?) {
        registry.listenerContainers.stream()
                .map { c -> c as AbstractMessageListenerContainer }
                .forEach { c -> c.setTaskExecutor(SimpleAsyncTaskExecutor(c.listenerId + "-")) }

        for (listenerContainer in registry.listenerContainers.map { c -> c as AbstractMessageListenerContainer }) {
            if (processControlRegistry == null || processControlRegistry.actionEnabled(ACTION_PREFIX + listenerContainer.listenerId)) {
                listenerContainer.start()
            } else {
                LOG.warn("Do not start ${listenerContainer.listenerId} as processing action is disabled")
            }
        }
    }

    override fun onApplicationEvent(event: ProcessControlEvent) {
        if (event.actionId.startsWith(ACTION_PREFIX)) {
            val listenerId = event.actionId.substringAfter(ACTION_PREFIX)
            val container = registry.getListenerContainer(listenerId)
            if (event.enabled) {
                LOG.info("Starting $listenerId")
                container.start()
            } else {
                LOG.warn("Stopping $listenerId")
                container.stop()
            }
        }
    }

    companion object {
        private const val ACTION_PREFIX = "rabbit-"
        private val LOG = LoggerFactory.getLogger(RabbitListenerControlService::class.java)
    }
}