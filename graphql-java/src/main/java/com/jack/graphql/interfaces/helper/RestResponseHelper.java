package com.jack.graphql.interfaces.helper;

public final class RestResponseHelper {

    private RestResponseHelper(){}


    public static <T> RestResponse<T> success(T data){
        return new RestResponse(RestResponseCode.SUCCESS.getCode(), RestResponseCode.SUCCESS.getMessage(), data);
    }

    public static <T> RestResponse<T> success(T data, String message){
        return new RestResponse(RestResponseCode.SUCCESS.getCode(), message, data);
    }

    public static <T> RestResponse<T> failed(){
        return new RestResponse(RestResponseCode.FAIL.getCode(), RestResponseCode.FAIL.getMessage(), null);
    }

    public static <T> RestResponse<T> failed(T data){
        return new RestResponse(RestResponseCode.FAIL.getCode(), RestResponseCode.FAIL.getMessage(), data);
    }

    public static <T> RestResponse<T> failed(T data, String message){
        return new RestResponse(RestResponseCode.FAIL.getCode(), message, data);
    }

    public static <T> RestResponse<T> failed(RestResponseCode restResponseCode, String message){
        return new RestResponse(restResponseCode.getCode(), message, null);
    }

    public static <T> RestResponse<T> failed(RestResponseCode restResponseCode, T data, String message){
        return new RestResponse(restResponseCode.getCode(), message, data);
    }

    public static <T> RestResponse<T> validateFailed() {
        return failed(RestResponseCode.VALIDATE_FAIL, RestResponseCode.VALIDATE_FAIL.getMessage());
    }

    public static <T> RestResponse<T> validateFailed(String message) {
        return failed(RestResponseCode.VALIDATE_FAIL, message);
    }

    public static <T> RestResponse<T> notFound() {
        return failed(RestResponseCode.NOT_FOUND, RestResponseCode.NOT_FOUND.getMessage());
    }

    public static <T> RestResponse<T> notFound(String message) {
        return failed(RestResponseCode.NOT_FOUND, message);
    }

    public static <T> RestResponse<T> unauthorized() {
        return failed(RestResponseCode.UNAUTHORIZED, RestResponseCode.UNAUTHORIZED.getMessage());
    }

    public static <T> RestResponse<T> unauthorized(String message) {
        return failed(RestResponseCode.NOT_FOUND, message);
    }

    public static <T> RestResponse<T> forbidden() {
        return failed(RestResponseCode.FORBIDDEN, RestResponseCode.UNAUTHORIZED.getMessage());
    }

    public static <T> RestResponse<T> forbidden(String message) {
        return failed(RestResponseCode.FORBIDDEN, message);
    }



}
