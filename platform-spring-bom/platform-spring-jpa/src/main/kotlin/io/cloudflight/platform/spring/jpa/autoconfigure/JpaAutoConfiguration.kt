package io.cloudflight.platform.spring.jpa.autoconfigure

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement(order = 2000)
@EnableConfigurationProperties(TransactionProperties::class)
class JpaAutoConfiguration {

    @Bean
    fun platformTransactionCustomizer(properties: TransactionProperties): TransactionCustomizer {
        return TransactionCustomizer(properties)
    }
}
