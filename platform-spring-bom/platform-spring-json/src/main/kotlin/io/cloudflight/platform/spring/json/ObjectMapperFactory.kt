package io.cloudflight.platform.spring.json

import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.kotlinModule

/**
 * @author Harald Radi (harald.radi@cloudflight.io)
 * @version 1.0
 */
object ObjectMapperFactory {
    fun createObjectMapper(): JsonMapper {
        return JsonMapper.builder()
            .addModule(kotlinModule())
            .build()
    }
}
