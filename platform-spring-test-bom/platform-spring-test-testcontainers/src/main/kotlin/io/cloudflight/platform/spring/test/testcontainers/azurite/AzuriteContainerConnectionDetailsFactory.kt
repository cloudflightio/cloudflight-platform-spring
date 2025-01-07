package io.cloudflight.platform.spring.test.testcontainers.azurite

import io.cloudflight.platform.spring.autoconfigure.azurite.AzuriteConnectionDetails
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionDetailsFactory
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionSource

class AzuriteContainerConnectionDetailsFactory :
    ContainerConnectionDetailsFactory<AzuriteContainer, AzuriteConnectionDetails>(
        emptyList(),
        "io.cloudflight.platform.spring.storage.azure.autoconfigure.PlatformAzureStorageBlobAutoConfiguration"
    ) {

    override fun getContainerConnectionDetails(source: ContainerConnectionSource<AzuriteContainer>): AzuriteConnectionDetails {
        return AzuriteContainerConnectionDetails(source)
    }

    private inner class AzuriteContainerConnectionDetails(source: ContainerConnectionSource<AzuriteContainer>) :
        ContainerConnectionDetails<AzuriteContainer>(source), AzuriteConnectionDetails {
        override val accountEndpoint: String
            get() = "http://" + container.host + ":" + container.getMappedPort(
                container.port
            ) + "/" + ACCOUNT_NAME
        override val accountName: String = ACCOUNT_NAME
        override val accountKey: String = ACCOUNT_KEY
    }

    companion object {
        /**
         * can't be changed, see [https://github.com/Azure/Azurite#default-storage-account](https://github.com/Azure/Azurite#default-storage-account)
         */
        internal const val ACCOUNT_NAME = "devstoreaccount1"

        /**
         * can't be changed, see [https://github.com/Azure/Azurite#default-storage-account](https://github.com/Azure/Azurite#default-storage-account)
         */
        internal const val ACCOUNT_KEY =
            "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw=="
    }
}