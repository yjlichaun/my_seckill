package com.muyi.my_seckill.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 历川
 * @version 1.0
 * @description 订单信息
 * @date 2023/4/18 19:27
 */
@Data
@TableName("order_info")
public class OrderInfo {
    //订单id

    private Long id;

    //用户id

    private Long userId;

    //商品id

    private Long goodsId;

    //商品名称

    private String goodsName;

    //商品数量

    private Integer goodsCount;

    //商品价格

    private BigDecimal goodsPrice;

    //订单状态

    private  Integer status;

    //订单创建日期

    private Date createDate;

    //支付日期

    private Date payDate;
}
