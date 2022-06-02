package io.cloudflight.platform.spring.storage.azure

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.cloudflight.platform.spring.context.ApplicationContextProfiles.TEST
import io.cloudflight.platform.spring.json.ObjectMapperFactory
import io.cloudflight.platform.spring.storage.dto.ObjectProperties
import io.cloudflight.platform.spring.storage.dto.StorageRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.util.*
import kotlin.io.path.deleteIfExists

@ActiveProfiles(value = [TEST])
@SpringBootTest
class ApplicationTest(
    @Autowired private val wac: WebApplicationContext,
    @Autowired private val objectMapper: ObjectMapper
) {

    private val mockMvc = MockMvcBuilders.webAppContextSetup(wac).build()

    @Test
    fun `upload and download through webserver`() {
        val containerName = "container-" + UUID.randomUUID()
        val blobName = "blob-" + UUID.randomUUID()

        mockMvc.put("/containers/${containerName}/${blobName}") {
            content = "Hello Azure"
            contentType = MediaType.TEXT_PLAIN
        }.andExpect {
            status { isOk() }
        }

        mockMvc.get("/containers/${containerName}/${blobName}").andExpect {
            status { isOk() }
            content {
                string("Hello Azure")
            }
        }
    }

    @Test
    fun `upload through webserver download from storage`() {
        val containerName = "container-" + UUID.randomUUID()
        val blobName = "blob-" + UUID.randomUUID()

        mockMvc.put("/containers/${containerName}/${blobName}") {
            content = "Hello Azure"
            contentType = MediaType.TEXT_PLAIN
        }.andExpect {
            status { isOk() }
        }

        val downloadUrl = mockMvc.get("/download-url/${containerName}/${blobName}").andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val get = HttpRequest.newBuilder(URI(downloadUrl)).GET().build()
        val getResponse = HttpClient.newHttpClient().send(get, HttpResponse.BodyHandlers.ofString())
        assertThat(getResponse.body()).isEqualTo("Hello Azure")
    }


    @Test
    fun `upload with direct access`() {
        val containerName = "container-" + UUID.randomUUID()
        val blobName = "blob-" + UUID.randomUUID()

        val contentType = MediaType.TEXT_PLAIN
        val body = HttpRequest.BodyPublishers.ofString("Hello Azure")
        upload(containerName, blobName, contentType, body)


        val downloadUrl = mockMvc.get("/download-url/${containerName}/${blobName}").andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val get = HttpRequest.newBuilder(URI(downloadUrl)).GET().build()
        val getResponse = HttpClient.newHttpClient().send(get, HttpResponse.BodyHandlers.ofString())
        assertThat(getResponse.body()).isEqualTo("Hello Azure")

        val getWithoutSas = HttpRequest.newBuilder(URI(downloadUrl.substringBeforeLast("?"))).GET().build()
        val getWithoutSasResponse = HttpClient.newHttpClient().send(getWithoutSas, HttpResponse.BodyHandlers.ofString())
        assertThat(getWithoutSasResponse.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value())
    }

    @Test
    fun `upload pdf with direct access`() {
        val containerName = "container-" + UUID.randomUUID()
        val blobName = "blob-" + UUID.randomUUID()

        val uploadFile = Path.of("src/test/resources/upload.pdf")
        upload(
            containerName,
            blobName,
            MediaType.APPLICATION_PDF,
            HttpRequest.BodyPublishers.ofFile(uploadFile)
        )

        val downloadUrl = mockMvc.get("/download-url/${containerName}/${blobName}").andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        val get = HttpRequest.newBuilder(URI(downloadUrl)).GET().build()
        val downloadFile = Path.of("build/download.pdf").also { it.deleteIfExists() }
        val getResponse = HttpClient.newHttpClient().send(get, HttpResponse.BodyHandlers.ofFile(downloadFile))
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value())
        assertThat(
            getResponse.headers().firstValue("Content-Type").get()
        ).isEqualTo(MediaType.APPLICATION_PDF.toString())
        assertThat(downloadFile.toFile()).hasSameBinaryContentAs(uploadFile.toFile())
    }

    @Test
    fun `override upload with direct access`() {
        val containerName = "container-" + UUID.randomUUID()
        val blobName = "blob-" + UUID.randomUUID()

        val post =
            upload(containerName, blobName, MediaType.TEXT_PLAIN, HttpRequest.BodyPublishers.ofString("Hello Azure"))

        val postOverrideResponse = HttpClient.newHttpClient().send(post, HttpResponse.BodyHandlers.ofString())
        assertThat(postOverrideResponse.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value())
    }

    @Test
    fun `mark pdf for deletion`() {
        val containerName = "container-" + UUID.randomUUID()
        val blobName = "blob-" + UUID.randomUUID()

        val uploadFile = Path.of("src/test/resources/upload.pdf")
        upload(containerName, blobName, MediaType.APPLICATION_PDF, HttpRequest.BodyPublishers.ofFile(uploadFile))

        mockMvc.get("/object-count/${containerName}").andExpect { content { 1 } }
        mockMvc.delete("/delete/${containerName}/${blobName}").andExpect { status { isOk() } }
        mockMvc.get("/object-count/${containerName}").andExpect { content { 0 } }
    }

    @Test
    fun `upload item and get its properties`() {
        val containerName = "container-" + UUID.randomUUID()
        val blobName = "blob-" + UUID.randomUUID()

        val uploadFile = Path.of("src/test/resources/upload.pdf")
        upload(
            containerName,
            blobName,
            MediaType.APPLICATION_PDF,
            HttpRequest.BodyPublishers.ofFile(uploadFile)
        )

        val mapper = ObjectMapperFactory.createObjectMapper()
        val json = mockMvc.get("/metadata/${containerName}/${blobName}")
            .andReturn().response.contentAsString

        val properties: ObjectProperties = mapper.readValue(json)
        assertThat(properties).isNotNull
    }

    private fun upload(
        containerName: String,
        blobName: String,
        contentType: MediaType,
        body: HttpRequest.BodyPublisher
    ): HttpRequest {
        val uploadUrl = objectMapper.readValue(
            mockMvc.get("/upload-url/${containerName}/${blobName}?contentType=$contentType").andExpect {
                status { isOk() }
            }.andReturn().response.contentAsString, StorageRequest::class.java
        )

        val postBuilder = HttpRequest.newBuilder(URI(uploadUrl.url)).PUT(body)
        uploadUrl.httpHeaders.forEach { (key, value) -> postBuilder.header(key, value) }
        val post = postBuilder.build()
        val postResponse = HttpClient.newHttpClient().send(post, HttpResponse.BodyHandlers.ofString())
        assertThat(postResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value())
        return post
    }
}
