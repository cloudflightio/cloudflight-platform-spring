package io.cloudflight.platform.spring.server

import java.time.Instant

/**
 * Inject this interface into your Spring Beans in order to get information about the current server module
 */
interface ServerModuleIdentification {
    /**
     * the group id of your server module
     */
    fun getGroup(): String

    /**
     * the name of your server module
     */
    fun getName(): String

    /**
     * The unique ID of your server module. In case you have git.properties on your classpath, this is the recent git hash
     */
    fun getId(): String

    /**
     * In case you have git.properties on your classpath, this is the recent git hash (shortened), otherwise it is null
     */
    fun getIdShort(): String?

    /**
     * In case you have git.properties on your classpath, this is time of recent git commit, otherwise it is null
     */
    fun getTime(): Instant?

    /**
     * The version of your server module as defined in your build system (i.e. Gradle)
     */
    fun getVersion(): String
}
