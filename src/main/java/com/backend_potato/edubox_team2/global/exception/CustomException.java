package com.backend_potato.edubox_team2.global.exception;

public class CustomException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public int getStatus() {
        return errorCode.getStatus();
    }

    public String getCode(){
        return errorCode.getCode();
    }

    public String getMessage(){
        return errorCode.getMessage();
    }
}
