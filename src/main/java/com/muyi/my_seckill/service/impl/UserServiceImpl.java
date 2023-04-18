package com.muyi.my_seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.muyi.my_seckill.common.CodeMsg;
import com.muyi.my_seckill.common.LoginParam;
import com.muyi.my_seckill.common.Result;
import com.muyi.my_seckill.dao.UserMapper;
import com.muyi.my_seckill.po.User;
import com.muyi.my_seckill.service.UserService;
import com.muyi.my_seckill.util.MD5Util;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/4/18 20:17
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public Result<User> login(LoginParam loginParam) {
        String mobile = loginParam.getMobile();
        String password = loginParam.getPassword();
        if (mobile == null || "".equals(mobile)) {
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        if (password == null || "".equals(password)){
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq("phone",mobile);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            return Result.error(CodeMsg.MOBILE_NOT_EXIST);
        }
        String salt = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(password, salt);
        if (!StringUtils.equals(user.getPassword(),calcPass)){
            return Result.error(CodeMsg.PASSWORD_ERROR);
        }
        user.setPassword(StringUtils.EMPTY);
        return Result.success(user);
    }
}
