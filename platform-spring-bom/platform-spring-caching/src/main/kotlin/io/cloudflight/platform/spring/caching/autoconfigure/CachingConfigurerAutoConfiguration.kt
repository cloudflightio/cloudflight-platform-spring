package io.cloudflight.platform.spring.caching.autoconfigure

import io.cloudflight.platform.spring.caching.EvictCacheErrorHandler
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.interceptor.CacheErrorHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Role
import org.springframework.data.redis.core.RedisOperations

@AutoConfiguration
@ConditionalOnClass(RedisOperations::class)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Import(CachingConfigurerAutoConfiguration.CachingConfigurerConfiguration::class)
class CachingConfigurerAutoConfiguration {
    @Configuration
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    class CachingConfigurerConfiguration : CachingConfigurer {
        @Bean
        // https://github.com/spring-projects/spring-security/issues/14209#issuecomment-1836854767
        // @Role annotation does not propagate so it is required to be on all Beans in this file
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        override fun errorHandler(): CacheErrorHandler {
            return EvictCacheErrorHandler()
        }
    }
}
