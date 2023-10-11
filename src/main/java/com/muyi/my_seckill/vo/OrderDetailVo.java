package com.muyi.my_seckill.vo;


import com.muyi.my_seckill.bo.GoodsBo;
import com.muyi.my_seckill.po.OrderInfo;
import lombok.Data;


/**
 * Created by: HuangFuBin
 * Date: 2018/7/19
 * Time: 0:40
 * Such description:
 */
@Data
public class OrderDetailVo {
    private GoodsBo goods;
    private OrderInfo order;
}
