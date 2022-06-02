package io.cloudflight.platform.spring.storage.azure.service

import com.azure.storage.blob.BlobClient
import com.azure.storage.blob.sas.BlobContainerSasPermission
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues
import java.time.Duration
import java.time.OffsetDateTime

internal class AzuriteSasTokenStrategy : SasTokenStrategy {

    override fun getSasToken(
        blobClient: BlobClient,
        validityDuration: Duration,
        permission: BlobContainerSasPermission?
    ): String {
        val accountSasValues = BlobServiceSasSignatureValues(
            OffsetDateTime.now().plus(validityDuration),
            permission
        )
        return blobClient.generateSas(accountSasValues)
    }
}