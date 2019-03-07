package com.example.common.exception;

public class UnLoginExceptionHandler extends RuntimeException{
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UnLoginExceptionHandler(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
