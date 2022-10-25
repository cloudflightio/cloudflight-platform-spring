package io.cloudflight.platform.spring.logging.mdc

interface MDCAccess {
    fun put(key: String, value: String?)
    fun put(entry: Pair<String, String?>)
    fun remove(key: String)
}

interface MDCScope {

    /**
     * we want to call this property MDC on purpose to give the caller inside
     * the kotlin extension function the impression it calls the static MDC class
     * from SLF4J
     */
    @Suppress("VariableNaming", "PropertyName")
    val MDC: MDCAccess
}
