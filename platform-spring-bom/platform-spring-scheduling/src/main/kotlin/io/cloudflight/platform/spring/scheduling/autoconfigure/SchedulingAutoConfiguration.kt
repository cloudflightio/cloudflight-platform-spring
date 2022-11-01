package io.cloudflight.platform.spring.scheduling.autoconfigure

import io.cloudflight.platform.spring.caching.autoconfigure.CachingAutoConfiguration
import io.cloudflight.platform.spring.scheduling.lock.NoopLockProvider
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.scheduling.annotation.EnableScheduling

@AutoConfiguration(after = [CachingAutoConfiguration::class, RedisAutoConfiguration::class])
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
@Import(value = [SchedulingAutoConfiguration.NoopConfiguration::class, SchedulingAutoConfiguration.RedisConfiguration::class])
class SchedulingAutoConfiguration {
    @Configuration
    @ConditionalOnBean(RedisConnectionFactory::class)
    class RedisConfiguration {
        @Bean
        fun lockProvider(connectionFactory: RedisConnectionFactory): LockProvider {
            return RedisLockProvider(connectionFactory)
        }
    }

    @Configuration
    @ConditionalOnMissingBean(RedisConnectionFactory::class)
    class NoopConfiguration {
        @Bean
        fun lockProvider(): LockProvider {
            return NoopLockProvider()
        }
    }
}
