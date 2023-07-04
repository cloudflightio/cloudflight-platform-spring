package io.cloudflight.platform.spring.test.testcontainers.azurite

import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

class AzuriteContainer(dockerImageName: DockerImageName) : GenericContainer<AzuriteContainer>(dockerImageName) {

    constructor() : this(DockerImageName.parse(DEFAULT_IMAGE_NAME))

    init {
        addExposedPort(AZURITE_PORT)
    }

    val port: Int = AZURITE_PORT

    val accountName = AzuriteContainerConnectionDetailsFactory.ACCOUNT_NAME
    val accountKey = AzuriteContainerConnectionDetailsFactory.ACCOUNT_KEY

    companion object {
        private const val DEFAULT_IMAGE_NAME = "mcr.microsoft.com/azure-storage/azurite"

        private const val AZURITE_PORT = 10000
    }
}