package io.cloudflight.platform.spring.validation.api.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(description = "validation messages on a field level")
data class FieldMessageDto(
        @ApiModelProperty("the field name where this validation message is applied")
        val field: String,
        private val fieldMessage: GlobalMessageDto
) : Message by fieldMessage
