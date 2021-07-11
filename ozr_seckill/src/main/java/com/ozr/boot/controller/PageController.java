package com.ozr.boot.controller;

/**
 * @Author OZR
 * @Date 2021/6/28 21:54
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/***
 * 这个controller处理页面的跳转
 */
@Controller
public class PageController {
    @GetMapping(value = {"/"})
    public String getIndex(){
        System.out.println("sdsdsdsdsds");
        return "getotp";
    }


    @GetMapping("/goRegister")
    public String goRegister(){
        return "register";
    }
}
