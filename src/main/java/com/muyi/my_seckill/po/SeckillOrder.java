package com.muyi.my_seckill.po;

import lombok.Data;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/4/25 12:40
 */
@Data
public class SeckillOrder {
    //id

    private Long id;
    //用户id

    private Long userId;
    //订单id

    private Long orderId;
    //商品id

    private Long goodsId;
}
