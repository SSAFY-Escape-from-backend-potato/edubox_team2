package com.backend_potato.edubox_team2.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    BAD_REQUEST(400,"C001","잘못된 요청입니다."),
    UNAUTHORIZED(401,"C002","로그인이 필요한 서비스입니다."),
    FORBIDDEN(403,"C003","접근 권한이 없습니다."),
    DATA_NOT_FOUND(404,"C004","응답 데이터를 찾을 수 없습니다."),
    REQUEST_BODY_MISSING(400,"C005","요청 데이터가 비어있습니다."),
    INVALID_PARAMETER(400,"C006","요청 파라미터가 잘못되었습니다."),
    METHOD_NOT_ALLOWED(400,"C007","요청 메서드가 잘못되었습니다."),

    INTERNAL_SERVER_ERROR(500,"500","서버에서 에러가 발생했습니다."),
    INSERT_ERROR(200,"S002","데이터 추가 실패"),
    UPDATE_ERROR(200,"S003","데이터 수정 실패"),
    DELETE_ERROR(200,"S004","데이터 삭제 실패");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
//https://adjh54.tistory.com/79