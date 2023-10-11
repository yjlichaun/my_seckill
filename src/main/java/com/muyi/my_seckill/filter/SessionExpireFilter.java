package com.muyi.my_seckill.filter;

import com.muyi.my_seckill.common.Const;
import com.muyi.my_seckill.po.User;
import com.muyi.my_seckill.redis.RedisService;
import com.muyi.my_seckill.redis.UserKey;
import com.muyi.my_seckill.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/5/8 19:29
 */
public class SessionExpireFilter implements Filter {

    @Autowired
    RedisService redisService;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String token = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(token)){
            //判断token是否为空或者"";
            //如果不为空，符合条件，继续获取user
            User user =  redisService.get(UserKey.getByName,token,User.class);
            if (user != null){
                //如果user ！= null ， 则重置session时间，即调用expire命令
                redisService.expire(UserKey.getByName,token, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
