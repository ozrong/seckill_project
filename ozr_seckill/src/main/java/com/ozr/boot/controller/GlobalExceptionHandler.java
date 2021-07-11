package com.ozr.boot.controller;


import com.ozr.boot.error.BusinessException;
import com.ozr.boot.error.EmBusinessError;
import com.ozr.boot.response.CommonRetrunType;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hzllb on 2018/12/22.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonRetrunType doError(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Exception ex) {

        Map<String,Object> responseData = new HashMap<>();
        if( ex instanceof BusinessException){
            BusinessException businessException = (BusinessException)ex;
            responseData.put("errCode",businessException.getErrCode());
            responseData.put("Msg",businessException.getErrMsg());
        }else if(ex instanceof ServletRequestBindingException){
            responseData.put("errCode",EmBusinessError.UNKNOW_ERROR.getErrCode());
            responseData.put("Msg","url绑定路由问题");
        }else if(ex instanceof NoHandlerFoundException){
            responseData.put("errCode",EmBusinessError.UNKNOW_ERROR.getErrCode());
            responseData.put("Msg","没有找到对应的访问路径");
        }else{
            responseData.put("errCode", EmBusinessError.UNKNOW_ERROR.getErrCode());
            responseData.put("Msg", EmBusinessError.UNKNOW_ERROR.getErrMsg());
        }
        return CommonRetrunType.creat(responseData,"fail");
    }
}
