package io.cloudflight.platform.spring.messaging

import org.springframework.context.ApplicationEvent

// TODO move to other package, add documentation
interface ProcessControlRegistry {
    fun actionEnabled(id: String): Boolean
}

data class ProcessControlEvent(val actionId: String, val enabled: Boolean) : ApplicationEvent(actionId)