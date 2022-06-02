package io.cloudflight.platform.spring.test.openfeign

import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.cloud.openfeign.FeignClientBuilder
import org.springframework.cloud.openfeign.FeignClientsConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

object FeignTestClientFactory {

    fun <T> createClientApi(
        apiClass: Class<T>,
        port: Int,
        path: String,
        clientContext: ApplicationContext = DEFAULT_CLIENT_CONTEXT
    ): T {
        return FeignClientBuilder(clientContext).forType(apiClass, apiClass.canonicalName)
            .url("http://localhost:$port/$path")
            .build()
    }

    fun <T> createClientApi(
        apiClass: Class<T>,
        port: Int,
        clientContext: ApplicationContext = DEFAULT_CLIENT_CONTEXT
    ): T {
        return createClientApi(apiClass, port,"", clientContext)
    }


    val DEFAULT_CLIENT_CONTEXT by lazy {
        AnnotationConfigApplicationContext(
            FeignAutoConfiguration::class.java,
            FeignClientsConfiguration::class.java,
            HttpMessageConvertersAutoConfiguration::class.java
        )
    }
}
