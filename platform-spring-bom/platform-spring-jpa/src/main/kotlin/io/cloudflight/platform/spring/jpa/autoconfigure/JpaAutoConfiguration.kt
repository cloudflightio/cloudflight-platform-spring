package io.cloudflight.platform.spring.jpa.autoconfigure

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * @author Harald Radi (harald.radi@cloudflight.io)
 * @version 1.0
 */
@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(TransactionProperties::class)
class JpaAutoConfiguration {

    @Bean
    fun platformTransactionCustomizer(properties: TransactionProperties): TransactionCustomizer {
        return TransactionCustomizer(properties)
    }
}
