package io.cloudflight.platform.spring.storage.dto

data class StorageRequest(
    val url: String,
    val requestType: StorageRequestType,
    val httpHeaders: Map<String, String>
)
