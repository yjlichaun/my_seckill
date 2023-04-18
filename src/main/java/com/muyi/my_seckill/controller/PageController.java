package com.muyi.my_seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 历川
 * @version 1.0
 * @description 登录页面加载
 * @date 2023/4/18 19:57
 */
@Controller
@RequestMapping("/page")
public class PageController {
    @RequestMapping("login")
    public String loginPage(){
        return "login";
    }
}
