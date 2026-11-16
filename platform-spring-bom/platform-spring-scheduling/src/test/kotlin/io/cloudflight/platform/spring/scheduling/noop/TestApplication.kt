package io.cloudflight.platform.spring.scheduling.noop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration

@SpringBootApplication(exclude = [DataRedisAutoConfiguration::class])
class TestApplication
