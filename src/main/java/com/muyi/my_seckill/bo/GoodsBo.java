package com.muyi.my_seckill.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/4/24 19:11
 */
@Data
public class GoodsBo {
    //秒杀价格

    private BigDecimal seckillPrice;
    //库存数量

    private Integer stockCount;
    //开始时间

    private Date startDate;
    //结束时间

    private Date endDate;
    //id

    private Long id;
    //物品名称

    private String goodsName;
    //物品标题

    private String goodsTitle;
    //物品图片

    private String goodsImg;
    //商品价格

    private BigDecimal goodsPrice;
    //商品库存

    private Integer goodsStock;
    //创建时间

    private Date createDate;
    //更新时间

    private Date updateDate;
    //商品介绍

    private String goodsDetail;
}
