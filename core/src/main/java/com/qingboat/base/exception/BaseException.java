package com.qingboat.base.exception;

import lombok.Data;

@Data
public class BaseException extends  RuntimeException{

    private static final long serialVersionUID = -3189734334155937168L;

    /**
     * 错误码
     */
    private int code;

    /**ApiResponse
     * 错误信息
     */
    private String message;


    public BaseException(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }
}
