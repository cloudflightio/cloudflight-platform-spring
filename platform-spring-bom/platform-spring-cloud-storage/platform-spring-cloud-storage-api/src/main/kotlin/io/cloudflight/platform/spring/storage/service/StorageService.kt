package io.cloudflight.platform.spring.storage.service

import io.cloudflight.platform.spring.storage.dto.*
import org.springframework.http.MediaType
import java.io.InputStream
import java.time.Duration

/**
 * The [StorageService] provides a common interface around cloud storages like Amazon S3 or Azure Storage.
 * It provides method for direct [StorageService.download] or [StorageService.upload] directly form the backend,
 * but also gives you the opportunity to create signed URLs towards your storage that you can pass to any client
 * to upload and download objects directly without going through your application server.
 *
 * As an abstraction we assume the underlying storage can handle containers (the equivalent in S3 are buckets)
 * and objects inside those containers, all of which have unique IDs.
 *
 * @author Klaus Lehner
 */
interface StorageService {

    /**
     * Generates a [StorageRequest] (typically a [StorageRequestType.GET]) towards the object at [storageLocation].
     * The link will be valid for the given [validityDuration].
     *
     * If the object does not exist, a [StorageObjectNotFoundException] is thrown.
     */
    @Throws(StorageObjectNotFoundException::class)
    fun generateDownloadRequest(storageLocation: StorageLocation, validityDuration: Duration): StorageRequest

    /**
     * Downloads the storage object at [storageLocation] by returning an [InputStream].
     *
     * It is the responsibility of the caller to close the stream.
     *
     * If the object does not exist, a [StorageObjectNotFoundException] is thrown.
     */
    @Throws(StorageObjectNotFoundException::class)
    fun download(storageLocation: StorageLocation): InputStream

    fun generateUploadRequest(
        storageLocation: StorageLocation,
        validityDuration: Duration,
        contentType: MediaType?
    ): StorageRequest

    fun upload(
        storageLocation: StorageLocation,
        inputStream: InputStream,
        contentType: MediaType,
        timeout: Duration = Duration.ofMinutes(1)
    )

    /**
     * Marks a storage object at the given [storageLocation] for deletion.
     * It's not guaranteed that the object is being physically deleted from the whole storage immediately.
     *
     * If the object does not exist, a [StorageObjectNotFoundException] is thrown.
     */
    @Throws(StorageObjectNotFoundException::class)
    fun markForDeletion(storageLocation: StorageLocation)

    /**
     * lists the name of all storage objects inside a given [container].
     * If the container does not exist, no container is being created and
     * an empty list is returned
     */
    fun listObjectNames(container: String, prefix: String? = null): Set<String>

    /**
     * Checks if a an object at given [storageLocation] exists.
     */
    fun existsObject(storageLocation: StorageLocation): Boolean

    /**
     * Gets the properties of a blob at a given [storageLocation]
     */
    fun getObjectProperties(storageLocation: StorageLocation): ObjectProperties
}

