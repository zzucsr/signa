package com.superSign.entity.pojo;

import lombok.Getter;
import lombok.Setter;

public class Result<T> {


    @Getter
    @Setter
    private int code = 0;

    @Getter
    @Setter
    private T data;

    @Getter
    @Setter
    private String msg;


    public Result() {
    }

    public Result(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}';
    }
}
