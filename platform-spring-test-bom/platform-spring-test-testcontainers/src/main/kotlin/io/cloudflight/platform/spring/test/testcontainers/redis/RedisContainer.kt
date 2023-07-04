package io.cloudflight.platform.spring.test.testcontainers.redis

import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

class RedisContainer(dockerImageName: DockerImageName) : GenericContainer<RedisContainer>(dockerImageName) {

    @Deprecated(message = "pass a dockerImageName")
    constructor() : this(DockerImageName.parse(DEFAULT_IMAGE_NAME).withTag(DEFAULT_TAG))

    init {
        addExposedPort(REDIS_PORT)
    }

    companion object {
        private val DEFAULT_IMAGE_NAME = "redis"

        private const val DEFAULT_TAG = "7.0.11"

        private const val REDIS_PORT = 6379
    }
}