package com.jack.graphql.interfaces.helper;

public enum RestResponseCode {
    SUCCESS(200, "success"),
    FAIL(400, "fail"),
    UNAUTHORIZED(401, "unauthorized"),
    FORBIDDEN(403, "forbidden"),
    NOT_FOUND(404, "cannot find data"),
    VALIDATE_FAIL(406, "validation failure"),
    SERVER_ERROR(500, "internal server error");

    private int code;
    private String message;

    private RestResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
