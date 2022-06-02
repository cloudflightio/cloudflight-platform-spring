package io.cloudflight.platform.spring.storage.azure.service

import com.azure.core.util.Context
import com.azure.storage.blob.BlobContainerClient
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.models.BlobHttpHeaders
import com.azure.storage.blob.models.ListBlobsOptions
import com.azure.storage.blob.options.BlobParallelUploadOptions
import com.azure.storage.blob.sas.BlobContainerSasPermission
import com.azure.storage.common.implementation.Constants
import io.cloudflight.platform.spring.storage.dto.*
import io.cloudflight.platform.spring.storage.service.StorageService
import org.springframework.http.MediaType
import java.io.InputStream
import java.time.Duration

class AzureStorageService(
    private val client: BlobServiceClient,
    private val sasTokenStrategy: SasTokenStrategy
) : StorageService {

    override fun generateDownloadRequest(
        storageLocation: StorageLocation,
        validityDuration: Duration
    ): StorageRequest {
        val url = getSasSignedUrl(
            storageLocation,
            validityDuration,
            BlobContainerSasPermission().setReadPermission(true),
            createContainer = false
        )
        return StorageRequest(url, StorageRequestType.GET, DOWNLOAD_HEADERS)
    }

    override fun download(
        storageLocation: StorageLocation
    ): InputStream {
        val containerClient = getContainer(storageLocation.container) ?: throw StorageObjectNotFoundException(storageLocation)
        return containerClient.getBlobClient(storageLocation.objectName).openInputStream()
    }

    override fun listObjectNames(container: String, prefix: String?): Set<String> {
        val containerClient = getContainer(container) ?: return emptySet()
        return containerClient.listBlobs(ListBlobsOptions().setPrefix(prefix), null).map { it.name }.toSet()
    }

    override fun generateUploadRequest(
        storageLocation: StorageLocation,
        validityDuration: Duration,
        contentType: MediaType?
    ): StorageRequest {
        val url = getSasSignedUrl(
            storageLocation,
            validityDuration,
            BlobContainerSasPermission().setCreatePermission(true),
            createContainer = true
        )
        return if (contentType == null) {
            StorageRequest(url, StorageRequestType.PUT, UPLOAD_HEADERS)
        } else {
            StorageRequest(
                url,
                StorageRequestType.PUT,
                UPLOAD_HEADERS + mapOf(Constants.HeaderConstants.CONTENT_TYPE to contentType.toString())
            )
        }
    }

    override fun upload(
        storageLocation: StorageLocation,
        inputStream: InputStream,
        contentType: MediaType,
        timeout: Duration
    ) {
        val client = createOrGetContainer(storageLocation.container)
        val blobClient = client.getBlobClient(storageLocation.objectName)

        val options = BlobParallelUploadOptions(inputStream)
        options.headers = BlobHttpHeaders().also {
            it.contentType = contentType.toString()
            it.contentDisposition = "attachment; filename=\"${storageLocation.objectName}\"" // TODO make optional/configurable
        }
        blobClient.uploadWithResponse(options, timeout, Context.NONE)
    }

    override fun markForDeletion(storageLocation: StorageLocation) {
        val containerClient = getContainer(storageLocation.container) ?: throw StorageObjectNotFoundException(storageLocation)
        val blobClient = containerClient.getBlobClient(storageLocation.objectName)

        if (!blobClient.exists()) throw StorageObjectNotFoundException(storageLocation)
        else blobClient.delete()
    }

    override fun existsObject(storageLocation: StorageLocation): Boolean {
        val containerClient = getContainer(storageLocation.container) ?: return false
        return containerClient.getBlobClient(storageLocation.objectName).exists()
    }

    override fun getObjectProperties(storageLocation: StorageLocation): ObjectProperties {
        val containerClient =
            getContainer(storageLocation.container) ?: throw StorageObjectNotFoundException(storageLocation)
        val blobClient = containerClient.getBlobClient(storageLocation.objectName)
        if (!blobClient.exists()) {
            throw StorageObjectNotFoundException(storageLocation)
        }
        val blobProperties = blobClient.properties
        return ObjectProperties(etag = blobProperties.eTag)
    }

    private fun getSasSignedUrl(
        storageLocation: StorageLocation,
        validityDuration: Duration,
        permission: BlobContainerSasPermission?,
        createContainer: Boolean
    ): String {
        // TODO cache the user delegation key and extend validity to X?
        val containerClient = if (createContainer)
            createOrGetContainer(storageLocation.container)
        else getContainer(storageLocation.container) ?: throw StorageObjectNotFoundException(storageLocation)
        val blobClient = containerClient.getBlobClient(storageLocation.objectName)
        return blobClient.blobUrl + "?" + sasTokenStrategy.getSasToken(blobClient, validityDuration, permission)
    }

    private fun createOrGetContainer(containerName: String): BlobContainerClient {
        // TODO caching
        val containerClient = client.getBlobContainerClient(containerName)
        if (!containerClient.exists()) {
            containerClient.create()
        }
        return containerClient
    }

    private fun getContainer(containerName: String): BlobContainerClient? {
        // TODO caching
        val containerClient = client.getBlobContainerClient(containerName)
        if (!containerClient.exists()) {
            return null
        }
        return containerClient
    }

    companion object {
        private val DOWNLOAD_HEADERS = emptyMap<String, String>()

        /**
         * This HTTP header is required when uploading a file to Azure via the REST API.
         *
         * For more information, check the [Azure Documentation](https://docs.microsoft.com/en-us/azure/media-services/previous/media-services-rest-upload-files#upload-a-file-with-postman).
         */
        private val UPLOAD_HEADERS = mapOf("x-ms-blob-type" to "BlockBlob")
    }
}
