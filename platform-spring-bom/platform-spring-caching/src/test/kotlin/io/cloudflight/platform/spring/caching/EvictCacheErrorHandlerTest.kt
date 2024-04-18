package io.cloudflight.platform.spring.caching

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.CacheErrorHandler
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.serializer.SerializationException
import java.util.concurrent.atomic.AtomicLong


class EvictCacheErrorHandlerTest {

    private var context: AnnotationConfigApplicationContext? = null

    private var cache: Cache? = null

    private var errorHandler: CacheErrorHandler? = null

    private var simpleService: SimpleService? = null


    @BeforeEach
    fun setup() {
        this.context = AnnotationConfigApplicationContext(Config::class.java)
        this.cache = context!!.getBean("mockCache", Cache::class.java)
        this.errorHandler = context!!.getBean(CacheErrorHandler::class.java)
        this.simpleService = context!!.getBean(SimpleService::class.java)
    }


    @AfterEach
    fun closeContext() {
        context!!.close()
    }

    @Test
    fun getFail() {
        val exception = SerializationException("Deserialization exception")
        willThrow(exception).given(this.cache)!!.get(0L)

        simpleService!!.get(0L)
        verify(this.cache!!).get(0L)
        verify(this.cache!!).evict(0L) // result of the invocation
    }

    @Test
    fun getFailException() {
        val exception = IllegalStateException("Wrong type exception")
        willThrow(exception).given(this.cache)!!.get(0L)

        assertThrows<IllegalStateException> { simpleService!!.get(0L) }
        verify(this.cache)!!.get(0L)
    }

    @Configuration
    @EnableCaching
    class Config : CachingConfigurer {
        @Bean
        override fun errorHandler(): CacheErrorHandler {
            return EvictCacheErrorHandler()
        }

        @Bean
        fun simpleService(): SimpleService {
            return SimpleService()
        }

        @Bean
        override fun cacheManager(): CacheManager {
            val cacheManager = SimpleCacheManager()
            cacheManager.setCaches(listOf(mockCache()))
            return cacheManager
        }

        @Bean
        fun mockCache(): Cache {
            val cache: Cache = mock()
            given(cache.name).willReturn("test")
            return cache
        }
    }

    @CacheConfig(cacheNames = ["test"])
    open class SimpleService {
        private val counter = AtomicLong()

        @Cacheable
        open fun get(id: Long): Any {
            return counter.getAndIncrement()
        }
    }
}
