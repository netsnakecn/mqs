package com.imboot.mqs.base;

public class BizException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String message;

    private int code;


    public BizException() {
    }

    public BizException(int c, String message) {
        this.code =c;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public BizException setCode(int c) {
        this.code = c;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public BizException setMessage(String message) {
        this.message = message;
        return this;
    }
}