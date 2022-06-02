package io.cloudflight.platform.spring.storage.dto

class StorageObjectNotFoundException(val storageLocation: StorageLocation) :
    RuntimeException("The object $storageLocation could not be found")