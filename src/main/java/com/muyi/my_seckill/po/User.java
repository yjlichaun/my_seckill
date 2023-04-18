package com.muyi.my_seckill.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.internal.ws.client.sei.ResponseBuilder;
import lombok.Data;

import java.util.Date;

/**
 * @author 历川
 * @version 1.0
 * @description 用户类
 * @date 2023/4/18 19:14
 */
@Data
@TableName("user")
public class User {
    //用户id

    private int id;

    //用户名

    private String userName;

    //手机号

    private String phone;

    //用户密码

    private String password;

    //

    private String salt;

    //

    private String head;

    //登录次数

    private int loginCount;

    //注册日期

    private Date registerDate;

    //上次登录日期

    private Date lastLoginDate;
}
