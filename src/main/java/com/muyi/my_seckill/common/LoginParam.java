package com.muyi.my_seckill.common;

import com.sun.istack.internal.NotNull;
import lombok.Data;
import org.apache.logging.log4j.message.Message;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

/**
 * @author 历川
 * @version 1.0
 * @description 登录信息
 * @date 2023/4/18 20:04
 */
@Data
public class LoginParam {
    //手机号
    @NotNull

    private String mobile;

    //密码
    @NotNull

    private String password;
}
