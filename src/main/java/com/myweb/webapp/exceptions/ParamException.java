package com.myweb.webapp.exceptions;

public class ParamException extends RuntimeException {
    public ParamException() {
        super("Missing required parameters");
    }
}
