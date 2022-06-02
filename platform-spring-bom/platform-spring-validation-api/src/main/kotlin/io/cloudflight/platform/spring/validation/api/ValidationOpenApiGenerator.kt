package io.cloudflight.platform.spring.validation.api

import io.cloudflight.platform.spring.validation.api.dto.ErrorResponse
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.web.bind.annotation.GetMapping

@Api("OpenApiGenerator", description = "Dummy api used to generated objects to the openapi spec")
interface ValidationOpenApiGenerator {

    @ApiOperation("Get an error response from the system")
    @GetMapping("/error")
    @ApiResponses(
            ApiResponse(code = 400, response = ErrorResponse::class, message = "Bad requests or validation exceptions"),
            ApiResponse(code = 500, response = ErrorResponse::class, message = "Internal server errors")
    )
    fun getErrorResponse(): ErrorResponse
}