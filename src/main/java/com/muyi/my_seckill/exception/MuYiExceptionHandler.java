package com.muyi.my_seckill.exception;

import com.muyi.my_seckill.common.CodeMsg;
import com.muyi.my_seckill.common.Result;
import com.sun.org.apache.bcel.internal.classfile.Code;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.BindException;
import java.util.List;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/5/8 19:20
 */
@ControllerAdvice
@ResponseBody
public class MuYiExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request,Exception ex){
        ex.printStackTrace();
        if (ex instanceof  MuYiException){
            MuYiException e = (MuYiException) ex;
            return Result.error(e.getCm());
        }else if (ex instanceof org.springframework.validation.BindException){
            org.springframework.validation.BindException e = (org.springframework.validation.BindException) ex;
            List<ObjectError> allErrors = e.getAllErrors();
            ObjectError error = allErrors.get(0);
            String message = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(message));
        }else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
