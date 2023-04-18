package com.muyi.my_seckill.service;

import com.muyi.my_seckill.common.LoginParam;
import com.muyi.my_seckill.common.Result;
import com.muyi.my_seckill.po.User;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/4/18 20:16
 */
public interface UserService {
    Result<User> login(LoginParam loginParam);
}
