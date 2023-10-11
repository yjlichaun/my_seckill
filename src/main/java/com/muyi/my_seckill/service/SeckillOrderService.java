package com.muyi.my_seckill.service;

import com.muyi.my_seckill.bo.GoodsBo;
import com.muyi.my_seckill.po.OrderInfo;
import com.muyi.my_seckill.po.SeckillOrder;
import com.muyi.my_seckill.po.User;

/**
 * @description 秒杀订单服务
 * @return
 * @author 历川
 * @date 2023/4/25 12:19
*/
public interface SeckillOrderService {

    boolean checkPath(User user, long goodsId, String path);

    SeckillOrder getSeckillOrderByUserIdAndGoodsId(long userId, long goodsId);

    OrderInfo insert(User user, GoodsBo goods);

    long getSeckillResult(long userId, long goodsId);

    String createMiaoshaPath(User user, long goodsId);

    OrderInfo getOrderInfo(long orderId);
}
