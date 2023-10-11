package com.muyi.my_seckill.service.impl;

import com.alibaba.druid.sql.visitor.functions.If;
import com.muyi.my_seckill.bo.GoodsBo;
import com.muyi.my_seckill.common.Const;
import com.muyi.my_seckill.dao.OrderInfoMapper;
import com.muyi.my_seckill.dao.SeckillOrderMapper;
import com.muyi.my_seckill.po.OrderInfo;
import com.muyi.my_seckill.po.SeckillOrder;
import com.muyi.my_seckill.po.User;
import com.muyi.my_seckill.redis.RedisService;
import com.muyi.my_seckill.redis.SeckillKey;
import com.muyi.my_seckill.service.OrderService;
import com.muyi.my_seckill.service.SeckillGoodsService;
import com.muyi.my_seckill.service.SeckillOrderService;
import com.muyi.my_seckill.util.MD5Util;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;

import javax.print.attribute.standard.MediaSize;
import java.util.Date;
import java.util.UUID;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/4/25 12:26
 */
@Service
@Slf4j
public class SeckillOrderServiceImpl implements SeckillOrderService {
    @Autowired
    RedisService redisService;

    @Autowired
    SeckillOrderMapper seckillOrderMapper;
    @Autowired
    SeckillGoodsService seckillGoodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Override
    public boolean checkPath(User user, long goodsId, String path) {
        if (user == null || path == null){
            return false;
        }
        String pathOld = redisService.get(SeckillKey.getSeckillPath,"" + user.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    @Override
    public SeckillOrder getSeckillOrderByUserIdAndGoodsId(long userId, long goodsId) {
        return seckillOrderMapper.selectByUserIdAndGoodId(userId,goodsId);
    }

    @Override
    @Transactional
    public OrderInfo insert(User user, GoodsBo goods) {
        //秒杀库存减一
        int success  = seckillGoodsService.reduceStock(goods.getId());
        if (success == 1){
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setCreateDate(new Date());
            orderInfo.setAddrId(0L);
            orderInfo.setGoodsCount(1);
            orderInfo.setGoodsId(goods.getId());
            orderInfo.setGoodsName(goods.getGoodsName());
            orderInfo.setGoodsPrice(goods.getSeckillPrice());
            orderInfo.setOrderChannel(1);
            orderInfo.setStatus(0);
            orderInfo.setUserId((long)user.getId());
            //添加信息进订单
            long orderId = orderService.addOrder(orderInfo);
            log.info("orderId --> " + orderId + "");
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setOrderId(orderId);
            seckillOrder.setGoodsId(goods.getId());
            seckillOrder.setUserId((long) user.getId());
            //插入秒杀表
            seckillOrderMapper.insert(seckillOrder);
            return orderInfo;
        }else {
            setGoodsOver(goods.getId());
            return null;
        }
    }

    @Override
    public long getSeckillResult(long userId, long goodsId) {
        SeckillOrder order = getSeckillOrderByUserIdAndGoodsId(userId, goodsId);
        if (order != null) {
            return  order.getOrderId();
        }else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver){
                return -1;
            }else {
                return 0;
            }
        }
    }

    @Override
    public String createMiaoshaPath(User user, long goodsId) {
        if (goodsId <= 0){
            return null;
        }
        String str = MD5Util.md5(UUID.randomUUID() + "muyi");
        redisService.set(SeckillKey.getSeckillPath,""+user.getId()+"_"+goodsId,str,Const.RedisCacheExtime.GOODS_ID);
        return str;
    }

    @Override
    public OrderInfo getOrderInfo(long orderId) {
        return orderInfoMapper.selectById(orderId);
    }

    /*
     * 查看秒杀商品是否已经结束
     * */
    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver,""+goodsId);
    }

    /*
     * 秒杀商品结束标记
     * */
    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, "" + goodsId, true, Const.RedisCacheExtime.GOODS_ID);
    }
}
