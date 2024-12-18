package com.backend_potato.edubox_team2.global.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SuccessResponse<T>{
    private T data;
    private int code;
    private String message;

    @Builder
    public SuccessResponse(T data, int code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }
}
