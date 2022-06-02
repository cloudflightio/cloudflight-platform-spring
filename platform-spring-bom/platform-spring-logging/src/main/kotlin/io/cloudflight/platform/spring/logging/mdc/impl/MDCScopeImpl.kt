package io.cloudflight.platform.spring.logging.mdc.impl

import io.cloudflight.platform.spring.logging.mdc.MDCAccess
import io.cloudflight.platform.spring.logging.mdc.MDCScope

private class MDCAccessImpl(private val mdcScope: MDCScopeImpl): MDCAccess by mdcScope

internal class MDCScopeImpl: MDCScope, MDCAccess {
    private val addedMDCEntries = mutableSetOf<String>()

    override val MDC: MDCAccess = MDCAccessImpl(this)

    override fun put(key: String, value: String) {
        addedMDCEntries.add(key)
        org.slf4j.MDC.put(key, value)
    }

    override fun put(entry: Pair<String, String>) {
        this.put(entry.first, entry.second)
    }

    override fun remove(key: String) {
        org.slf4j.MDC.remove(key)
        addedMDCEntries.remove(key)
    }

    fun removeAllAdded() {
        addedMDCEntries.forEach {
            org.slf4j.MDC.remove(it)
        }
    }
}
