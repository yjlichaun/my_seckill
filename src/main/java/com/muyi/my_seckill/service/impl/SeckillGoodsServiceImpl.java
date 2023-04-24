package com.muyi.my_seckill.service.impl;

import com.muyi.my_seckill.bo.GoodsBo;
import com.muyi.my_seckill.dao.GoodsMapper;
import com.muyi.my_seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/4/24 19:18
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    @Autowired
    GoodsMapper goodsMapper;
    @Override
    public List<GoodsBo> getSeckillGoodsList() {
        return goodsMapper.selectAllGoodes();
    }

    @Override
    public GoodsBo getSeckillGoodsBoByGoodsId(long goodsId) {
        return goodsMapper.getSeckillGoodsBoByGoodsId(goodsId);
    }
}
