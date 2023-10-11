package com.muyi.my_seckill.exception;

import com.muyi.my_seckill.common.CodeMsg;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/5/8 19:16
 */
public class MuYiException extends RuntimeException{
    private static final long serialVersionUID = 1l;
    private CodeMsg cm;
    public MuYiException(CodeMsg cm){
        super(cm.toString());
        this.cm = cm;
    }
    public CodeMsg getCm(){
        return cm;
    }
}
