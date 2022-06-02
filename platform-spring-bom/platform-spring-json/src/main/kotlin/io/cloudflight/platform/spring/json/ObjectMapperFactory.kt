package io.cloudflight.platform.spring.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

/**
 * @author Harald Radi (harald.radi@cloudflight.io)
 * @version 1.0
 */
object ObjectMapperFactory {
    fun createObjectMapper(): ObjectMapper {
        return ObjectMapper()
            .registerModule(JavaTimeModule())
            .registerKotlinModule()
    }
}
