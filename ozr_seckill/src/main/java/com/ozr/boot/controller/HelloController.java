package com.ozr.boot.controller;

import com.ozr.boot.dataObject.UserDao;
import com.ozr.boot.response.CommonRetrunType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author OZR
 * @Date 2021/6/25 16:11
 */
@Controller
public class HelloController {
//    @Autowired
//    UserServer userServer;
//
//    @GetMapping("/hello")
//    public String hello(){
//        return "hello";
//    }
//
    @ResponseBody
    @GetMapping("/getUser")
    public CommonRetrunType getUserById(){
          return  CommonRetrunType.creat("dadas");

    }


}
