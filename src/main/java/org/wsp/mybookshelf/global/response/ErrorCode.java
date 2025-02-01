package org.wsp.mybookshelf.global.response;

public enum ErrorCode {
    INVALID_REQUEST("400", "잘못된 요청입니다."),
    NOT_FOUND("404", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR("500", "서버 내부 오류입니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}