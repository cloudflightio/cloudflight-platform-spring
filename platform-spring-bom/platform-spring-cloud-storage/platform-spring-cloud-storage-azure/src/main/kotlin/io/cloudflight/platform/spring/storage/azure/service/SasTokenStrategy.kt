package io.cloudflight.platform.spring.storage.azure.service

import com.azure.storage.blob.BlobClient
import com.azure.storage.blob.sas.BlobContainerSasPermission
import java.time.Duration

interface SasTokenStrategy {

    fun getSasToken(
        blobClient: BlobClient,
        validityDuration: Duration,
        permission: BlobContainerSasPermission?
    ): String
}