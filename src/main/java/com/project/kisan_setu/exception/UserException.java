package com.project.kisan_setu.exception;
import jakarta.annotation.Generated;
import org.springframework.http.HttpStatus;


public class UserException extends RuntimeException {
    private HttpStatus status;
    public UserException(String message ,HttpStatus status) {
        super(message);
        this.status = status;
    }
    public HttpStatus getStatus() {
        return status;
    }

}
