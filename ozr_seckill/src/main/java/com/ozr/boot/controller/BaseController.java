package com.ozr.boot.controller;

import com.ozr.boot.error.BusinessException;
import com.ozr.boot.error.EmBusinessError;
import com.ozr.boot.response.CommonRetrunType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author OZR
 * @Date 2021/6/28 14:50
 */
public class BaseController {
    /**
     * 这种方式本来返回的是一个页面，而不是json
     * 但是我们在在类上使用的是@RestController，也即使添加@ResponseBod让他返回json
     */
//    @ResponseBody
//    @ExceptionHandler(Exception.class) //指定什么样的异常才执行这个方法，这里我们直接让所有的都进来了
//    @ResponseStatus(HttpStatus.OK) //指定状态码
//    public Object handlerException(HttpServletRequest request, Exception ex) {
//        Map<String, Object> responsemap = new HashMap<>();
//        if (ex instanceof BusinessException) { //异常包含我们的定义的异常才这样处理
//            BusinessException businessException = (BusinessException) ex;//强转
//            responsemap.put("errCode", businessException.getErrCode());
////        businessException.setErrMsg("hahahahha") 也可以自定义的设置错误提示信息
//            responsemap.put("Msg", businessException.getErrMsg());
//
////            CommonRetrunType commonRetrunType = new CommonRetrunType();
////            commonRetrunType.setStatus("fail");
////            commonRetrunType.setData(responsemap);
////        System.out.println("错误了");
//
//        }else{
//            responsemap.put("errCode", EmBusinessError.UNKNOW_ERROR.getErrCode());
////          businessException.setErrMsg("hahahahha") 也可以自定义的设置错误提示信息
//            responsemap.put("Msg", EmBusinessError.UNKNOW_ERROR.getErrMsg());
//        }
//        return CommonRetrunType.creat(responsemap,"fail");
//    }
}
