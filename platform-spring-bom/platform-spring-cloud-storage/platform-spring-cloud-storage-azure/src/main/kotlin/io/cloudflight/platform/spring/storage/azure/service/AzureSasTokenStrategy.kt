package io.cloudflight.platform.spring.storage.azure.service

import com.azure.storage.blob.BlobClient
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.sas.BlobContainerSasPermission
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues
import java.time.Duration
import java.time.OffsetDateTime

internal class AzureSasTokenStrategy(private val client: BlobServiceClient) : SasTokenStrategy {

    override fun getSasToken(
        blobClient: BlobClient,
        validityDuration: Duration,
        permission: BlobContainerSasPermission?
    ): String {
        val accountSasValues = BlobServiceSasSignatureValues(
            OffsetDateTime.now().plus(validityDuration),
            permission
        )
        // TODO cache the user delegation key and extend validity to X?
        val userDelegationKey = client.getUserDelegationKey(null, OffsetDateTime.now().plus(validityDuration))
        return blobClient.generateUserDelegationSas(accountSasValues, userDelegationKey)
    }
}