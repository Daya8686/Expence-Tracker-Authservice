package com.expencetracker.authservice.exceptions;

import org.springframework.http.HttpStatus;

public class RefreshTokenExceptionHandler extends RuntimeException {

	private HttpStatus httpStatus;

	public RefreshTokenExceptionHandler(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

}
