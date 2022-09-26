package io.cloudflight.platform.spring.caching

import io.cloudflight.platform.spring.caching.autoconfigure.CachingAutoConfiguration
import org.junit.jupiter.api.Test

class CachingAutoConfigurationTest {
    @Test
    fun testRedisTypeInfoSerialization() {
        val serializer = CachingAutoConfiguration.createJsonSerializer()

        val data = DataDto("foo")
        val bytes = serializer.serialize(data)
        val obj = serializer.deserialize(bytes, java.lang.Object::class.java) as DataDto

        assert(data.foo == obj.foo)
    }
}

data class DataDto(var foo: String)