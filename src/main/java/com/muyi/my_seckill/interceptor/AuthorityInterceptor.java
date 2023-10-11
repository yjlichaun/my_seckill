package com.muyi.my_seckill.interceptor;

import com.alibaba.fastjson.JSON;
import com.muyi.my_seckill.annotations.AccessLimit;
import com.muyi.my_seckill.common.CodeMsg;
import com.muyi.my_seckill.common.Result;
import com.muyi.my_seckill.po.User;
import com.muyi.my_seckill.redis.AccessKey;
import com.muyi.my_seckill.redis.RedisService;
import com.muyi.my_seckill.redis.UserKey;
import com.muyi.my_seckill.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @author 历川
 * @version 1.0
 * @description 拦截器统一校验用户
 * @date 2023/5/8 19:37
 */
@Component
public class AuthorityInterceptor implements HandlerInterceptor {
    @Autowired
    RedisService redisService;
    private final Logger logger = LoggerFactory.getLogger(AuthorityInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            //请求controller中的方法名
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //解析HandlerMethod
            String methodName = handlerMethod.getMethod().getName();
            String className = handlerMethod.getBean().getClass().getSimpleName();
            StringBuilder requestParamBuilder = new StringBuilder();
            Map paramMap = request.getParameterMap();
            Iterator iterator = paramMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry entry = (Map.Entry) iterator.next();
                String mapKey = (String) entry.getKey();
                String mapValue = "";
                //request的这个参数map的value返回的是一个sring[]
                Object value = entry.getValue();
                if (value instanceof String[]){
                    String[] strings = (String[]) value;
                    mapValue = Arrays.toString(strings);
                }
                requestParamBuilder.append(mapKey).append("=").append(mapValue);
            }
            //接口限流
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null){
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            //对于拦截器中拦截的manage下的login，do的处理，对于登录不拦截，直接放行
            if(!StringUtils.equals(className,"SeckillController")){
                //如果是拦截到登录请求，不打印参数，参数里有密码，防止日志泄露
                logger.info("权限拦截器拦截到请求 SeckillController , class:{} ,methodName:{}",className,methodName);
                return true;
            }
            logger.info("--> 权限拦截器拦截到请求,className:{},methodName:{},param:{}",className,methodName,requestParamBuilder);
            User user = null;
            String loginToken = CookieUtil.readLoginToken(request);
            if (StringUtils.isNotEmpty(loginToken)){
                user = redisService.get(UserKey.getByName,loginToken,User.class);
            }
            if (needLogin){
                if (user == null){
                    render(response,CodeMsg.USER_NO_LOGIN);
                    return false;
                }
                key += "_" + user.getId();
            }else {
                //do nothing
            }
            AccessKey ak = AccessKey.withExpire;
            Integer count = redisService.get(ak, key, Integer.class);
            if (count == null){
                redisService.set(ak,key,1,seconds);
            }else if (count < maxCount){
                redisService.incr(ak,key);
            }else {
                render(response,CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
    private void render(HttpServletResponse response, CodeMsg cm) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }
}
