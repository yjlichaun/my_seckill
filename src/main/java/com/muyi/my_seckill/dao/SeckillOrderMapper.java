package com.muyi.my_seckill.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muyi.my_seckill.po.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/4/25 12:42
 */
@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {

    SeckillOrder selectByUserIdAndGoodId(long userId, long goodsId);
}
