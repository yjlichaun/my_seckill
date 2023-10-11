package com.muyi.my_seckill.mq;



import com.muyi.my_seckill.bo.GoodsBo;
import com.muyi.my_seckill.po.SeckillOrder;
import com.muyi.my_seckill.po.User;
import com.muyi.my_seckill.redis.RedisService;
import com.muyi.my_seckill.service.OrderService;
import com.muyi.my_seckill.service.SeckillGoodsService;
import com.muyi.my_seckill.service.SeckillOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static final Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    RedisService redisService;

    @Autowired
    SeckillGoodsService goodsService;

    @Autowired
    SeckillOrderService seckillOrderService;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message) {
        // todo 如果这里出现异常可以进行补偿，重试，重新执行此逻辑，如果超过一定次数还是失败可以将此秒杀置为无效，恢复redis库存
        log.info("receive message:" + message);
        SeckillMessage mm = RedisService.stringToBean(message, SeckillMessage.class);
        User user = mm.getUser();
        long goodsId = mm.getGoodsId();

        GoodsBo goods = goodsService.getSeckillGoodsBoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        seckillOrderService.insert(user, goods);
    }
}
