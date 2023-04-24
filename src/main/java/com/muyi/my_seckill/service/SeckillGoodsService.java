package com.muyi.my_seckill.service;

import com.muyi.my_seckill.bo.GoodsBo;

import java.util.List;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/4/24 19:15
 */
public interface SeckillGoodsService {

    /***
     * @description 获取商品列表
     * @return java.util.List<com.muyi.my_seckill.bo.GoodsBo> 商品列表
     * @author 历川
     * @date 2023/4/24 19:16
    */
    List<GoodsBo> getSeckillGoodsList();

    GoodsBo getSeckillGoodsBoByGoodsId(long goodsId);
}
