package io.cloudflight.platform.spring.test.openfeign

import io.swagger.annotations.Api
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)     // <1>
class FeignTestClientFactoryTest(
    @Autowired @LocalServerPort private val port: Int                           // <2>
) {

    private val helloApi =
        FeignTestClientFactory.createClientApi(HelloWorldApi::class.java, port) // <3>

    @Test
    fun helloWorld() {
        assertThat(helloApi.helloWorld("John").name).isEqualTo("John")
    }
}

// All subsequent classes usually come from the application itself, you don't need
// them in your test classes. We just want to give an impression here of what we are
// testing here

@SpringBootApplication
class TestApplication                                                           // <4>

@Api("Project")
interface HelloWorldApi {                                                       // <5>

    @GetMapping("/hello/world")
    fun helloWorld(@RequestParam("name") name: String): HelloWorldDto
}

data class HelloWorldDto(val name: String, val time: LocalDateTime)

@RestController
class HelloWorldController : HelloWorldApi {                                    // <6>
    override fun helloWorld(name: String): HelloWorldDto {
        return HelloWorldDto(name, LocalDateTime.now())
    }
}

