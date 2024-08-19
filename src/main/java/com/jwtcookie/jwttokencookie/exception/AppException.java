package com.jwtcookie.jwttokencookie.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final HttpStatus status;
    private final String message;

    public AppException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
