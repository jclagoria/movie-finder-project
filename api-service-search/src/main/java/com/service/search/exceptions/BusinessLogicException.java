package com.service.search.exceptions;

import org.springframework.http.HttpStatusCode;

public class BusinessLogicException extends RuntimeException {

    private int code;

    public BusinessLogicException(String message, int code) {
        super(message);
        this.code = code;
    }

  public int getCode() {
    return code;
  }
}
