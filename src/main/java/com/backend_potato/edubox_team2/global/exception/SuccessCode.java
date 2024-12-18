package com.backend_potato.edubox_team2.global.exception;

import lombok.Getter;

@Getter
public enum SuccessCode {
    SELECT_SUCCESS(200,"200","데이터 조회 성공"),
    INSERT_SUCCESS(201,"201","데이터 추가 성공"),
    DELETE_SUCCESS(204,"204","데이터 삭제 성공"),
    UPDATE_SUCCESS(200,"200","데이터 수정 성공");

    private final int status;
    private final String code;
    private final String message;
    SuccessCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
