package io.cloudflight.platform.spring.scheduling.noop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration

@SpringBootApplication(exclude = [RedisAutoConfiguration::class])
class TestApplication
