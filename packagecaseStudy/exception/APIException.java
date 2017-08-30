package com.packagecaseStudy.exception;

/**
 * Created by Karthikeyan Rajasekar
 */
public class APIException extends RuntimeException {

    public APIException(String message) {
        super(message);
    }

    public APIException(String message, Throwable cause) {
        super(message, cause);
    }

    public APIException(Throwable cause) {
        super(cause);
    }
}
