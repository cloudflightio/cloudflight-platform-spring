package io.cloudflight.platform.spring.logging.mdc

import io.cloudflight.platform.spring.logging.mdc.impl.MDCScopeImpl

fun <T>mdcScope(body: MDCScope.() -> T): T {
    val mdcScope = MDCScopeImpl()
    try {
        return mdcScope.body()
    } finally {
        mdcScope.removeAllAdded()
    }
}
