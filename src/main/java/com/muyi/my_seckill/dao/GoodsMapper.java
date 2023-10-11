package com.muyi.my_seckill.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muyi.my_seckill.bo.GoodsBo;
import com.muyi.my_seckill.po.Goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/4/18 19:48
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
    List<GoodsBo> selectAllGoodes();

    GoodsBo getSeckillGoodsBoByGoodsId(long goodsId);

    int updateStock(Long goodsId);
}
