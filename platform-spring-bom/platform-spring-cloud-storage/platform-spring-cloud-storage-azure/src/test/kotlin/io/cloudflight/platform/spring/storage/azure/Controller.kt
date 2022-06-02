package io.cloudflight.platform.spring.storage.azure

import com.azure.storage.blob.BlobContainerClient
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import io.cloudflight.platform.spring.storage.dto.ObjectProperties
import io.cloudflight.platform.spring.storage.dto.StorageLocation
import io.cloudflight.platform.spring.storage.dto.StorageRequest
import io.cloudflight.platform.spring.storage.service.StorageService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayInputStream
import java.time.Duration

@RestController
class Controller(blobServiceClientBuilder: BlobServiceClientBuilder, private val storageService: StorageService) {

    private val client: BlobServiceClient = blobServiceClientBuilder.buildClient()

    @PutMapping("containers/{containerName}/{blobName}", consumes = ["text/plain"])
    fun postContent(
        @PathVariable containerName: String,
        @PathVariable blobName: String,
        @RequestBody content: String
    ): ResponseEntity<String> {
        storageService.upload(
            StorageLocation(containerName, blobName),
            ByteArrayInputStream(content.toByteArray()),
            MediaType.TEXT_PLAIN
        )
        return ResponseEntity.ok("")
    }

    @GetMapping("containers/{containerName}/{blobName}", produces = ["text/plain"])
    fun getContent(@PathVariable containerName: String, @PathVariable blobName: String) =
        createOrGetContainer(containerName).getText(blobName)

    @GetMapping("object-count/{containerName}", produces = ["text/plain"])
    fun getContent(@PathVariable containerName: String): Int = storageService.listObjectNames(containerName).size

    @GetMapping("download-url/{containerName}/{blobName}", produces = ["text/plain"])
    fun getDownloadUrl(@PathVariable containerName: String, @PathVariable blobName: String): String {
        return storageService.generateDownloadRequest(
            StorageLocation(containerName, blobName),
            Duration.ofMinutes(10)
        ).url
    }

    @GetMapping("upload-url/{containerName}/{blobName}")
    fun getUploadUrl(
        @PathVariable containerName: String,
        @PathVariable blobName: String,
        @RequestParam contentType: MediaType
    ): StorageRequest {
        return storageService.generateUploadRequest(
            StorageLocation(containerName, blobName),
            Duration.ofMinutes(10),
            contentType
        )
    }

    @DeleteMapping("delete/{containerName}/{blobName}")
    fun markForDeletion(
        @PathVariable containerName: String,
        @PathVariable blobName: String,
    ) {
        storageService.markForDeletion(
            StorageLocation(containerName, blobName)
        )
    }

    @GetMapping("metadata/{containerName}/{blobName}")
    fun getBlobMetadata(
        @PathVariable containerName: String,
        @PathVariable blobName: String
    ): ObjectProperties {
        return storageService.getObjectProperties(
            StorageLocation(containerName, blobName)
        )
    }

    fun createOrGetContainer(containerName: String): BlobContainerClient {
        // TODO caching
        val containerClient = client.getBlobContainerClient(containerName)
        if (!containerClient.exists()) {
            containerClient.create()
        }
        return containerClient
    }

    fun BlobContainerClient.getText(blobName: String) =
        getBlobClient(blobName).openInputStream().use { String(it.readAllBytes()) }
}
