package io.cloudflight.platform.spring.storage.azure.autoconfigure

import com.azure.core.credential.AzureNamedKeyCredential
import com.azure.core.credential.TokenCredential
import com.azure.identity.DefaultAzureCredentialBuilder
import com.azure.spring.cloud.autoconfigure.implementation.context.AzureContextUtils.STORAGE_BLOB_CLIENT_BUILDER_BEAN_NAME
import com.azure.spring.cloud.autoconfigure.implementation.storage.blob.AzureStorageBlobAutoConfiguration
import com.azure.spring.cloud.autoconfigure.implementation.storage.blob.properties.AzureStorageBlobProperties
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import io.cloudflight.platform.spring.autoconfigure.azurite.AzuriteConnectionDetails
import io.cloudflight.platform.spring.context.ApplicationContextProfiles
import io.cloudflight.platform.spring.storage.azure.service.AzureSasTokenStrategy
import io.cloudflight.platform.spring.storage.azure.service.AzureStorageService
import io.cloudflight.platform.spring.storage.azure.service.AzuriteSasTokenStrategy
import io.cloudflight.platform.spring.storage.azure.service.SasTokenStrategy
import io.cloudflight.platform.spring.storage.service.StorageService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

/**
 * Provides a different [BlobServiceClientBuilder] than the one defined in [AzureStorageBlobAutoConfiguration]
 * when the staging or production profile is active in order to connect to the azure storage using the
 * [DefaultAzureCredentialBuilder] instead of connecting to the local azurite storage.
 */
@AutoConfiguration(before = [AzureStorageBlobAutoConfiguration::class])
@ConditionalOnProperty(value = ["spring.cloud.azure.storage.blob.enabled"], havingValue = "true", matchIfMissing = true)
class PlatformAzureStorageBlobAutoConfiguration {

    @Bean(name = [STORAGE_BLOB_CLIENT_BUILDER_BEAN_NAME])
    @Profile(ApplicationContextProfiles.PRODUCTION, ApplicationContextProfiles.STAGING)
    fun blobServiceClientBuilder(
        storageProperties: AzureStorageBlobProperties,
        tokenCredential: TokenCredential
    ): BlobServiceClientBuilder {
        return BlobServiceClientBuilder()
            .endpoint(storageProperties.endpoint)
            .credential(tokenCredential)
    }

    @Bean(name = [STORAGE_BLOB_CLIENT_BUILDER_BEAN_NAME])
    @ConditionalOnBean(value = [AzuriteConnectionDetails::class])
    fun azuriteBlobServiceClientBuilder(connectionDetails: AzuriteConnectionDetails): BlobServiceClientBuilder {
        return BlobServiceClientBuilder()
            .endpoint(connectionDetails.accountEndpoint)
            .credential(AzureNamedKeyCredential(connectionDetails.accountName, connectionDetails.accountKey))
    }

    @Bean
    @ConditionalOnMissingBean
    fun tokenCredential(): TokenCredential {
        return DefaultAzureCredentialBuilder().build()
    }

    @Bean
    fun blobServiceClient(blobServiceClientBuilder: BlobServiceClientBuilder): BlobServiceClient {
        return blobServiceClientBuilder.buildClient()
    }

    @Bean
    fun azureStorageService(client: BlobServiceClient, sasTokenStrategy: SasTokenStrategy): StorageService {
        return AzureStorageService(client, sasTokenStrategy)
    }

    @Bean
    @Profile(ApplicationContextProfiles.DEVELOPMENT, ApplicationContextProfiles.TEST)
    fun azuriteTokenStrategy(): SasTokenStrategy {
        return AzuriteSasTokenStrategy()
    }

    @Bean
    @Profile(ApplicationContextProfiles.PRODUCTION, ApplicationContextProfiles.STAGING)
    fun azureTokenStrategy(client: BlobServiceClient): SasTokenStrategy {
        return AzureSasTokenStrategy(client)
    }
}