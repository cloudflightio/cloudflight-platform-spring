package io.cloudflight.platform.spring.autoconfigure.azurite

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails

interface AzuriteConnectionDetails : ConnectionDetails {

    val accountEndpoint: String

    val accountName: String

    val accountKey: String
}