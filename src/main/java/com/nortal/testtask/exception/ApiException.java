package com.nortal.testtask.exception;

import org.springframework.http.HttpStatus;

public record ApiException(HttpStatus status, String message) {
}
