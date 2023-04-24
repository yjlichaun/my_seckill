package com.muyi.my_seckill.vo;

import com.muyi.my_seckill.bo.GoodsBo;
import com.muyi.my_seckill.po.User;
import lombok.Data;

/**
 * @author 历川
 * @version 1.0
 * @description 商品介绍类
 * @date 2023/4/24 20:26
 */
@Data
public class GoodsDetailVo {
    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsBo goods ;
    private User user;
}
