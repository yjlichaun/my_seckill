package com.muyi.my_seckill.controller;

import com.muyi.my_seckill.common.Const;
import com.muyi.my_seckill.common.LoginParam;
import com.muyi.my_seckill.common.Result;
import com.muyi.my_seckill.po.User;
import com.muyi.my_seckill.redis.RedisService;
import com.muyi.my_seckill.redis.UserKey;
import com.muyi.my_seckill.service.UserService;
import com.muyi.my_seckill.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/4/18 19:59
 */
@Controller
@RequestMapping("/user")
public class LoginController {
    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;
    @RequestMapping("/login")
    @ResponseBody
    public Result<User> doLogin(HttpServletResponse response, HttpSession session, LoginParam loginParam){
        Result<User> loginUser = userService.login(loginParam);
        if (loginUser.isSuccess()){
            //token写入cookie
            CookieUtil.writeLoginToken(response,session.getId());
            //写入redis缓存
            redisService.set(UserKey.getByName,session.getId(),loginUser.getData(), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return loginUser;
    }
    @RequestMapping("logout")
    public String doLogout(HttpServletRequest request,HttpServletResponse response){
        String token = CookieUtil.readLoginToken(request);
        CookieUtil.delLoginToken(request,response);
        redisService.del(UserKey.getByName,token);
        return "login";
    }
}
