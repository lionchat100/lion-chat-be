package com.lion.be.global.controller.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorResponse {
	String code;
	String message;
}
