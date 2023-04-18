package com.muyi.my_seckill.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/4/18 20:09
 */
@Getter
@Setter
public class Result<T>{
    private int code;
    private String msg;
    private T data;
    public boolean isSuccess(){
        return this.code == CodeMsg.SUCCESS.getCode();
    }
    public static  <T> Result<T> success(T data){
        return new Result<T>(data);
    }
    public static  <T> Result<T> error(CodeMsg codeMsg){
        return new Result<T>(codeMsg);
    }
    private Result(T data) {
        this.code = CodeMsg.SUCCESS.getCode();
        this.msg = CodeMsg.SUCCESS.getMsg();
        this.data = data;
    }

    private Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Result(CodeMsg codeMsg) {
        if(codeMsg != null) {
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
        }
    }
}
