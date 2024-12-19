package com.backend_potato.edubox_team2.global.exception;

import lombok.Builder;
import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private int status;
    private String code;
    private String message;
    private String causeMessage;

    @Builder
    protected ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    @Builder
    protected ErrorResponse(ErrorCode code, String causeMessage) {
        this.status = code.getStatus();
        this.code = code.getCode();
        this.message = code.getMessage();
        this.causeMessage = causeMessage;
    }
}
