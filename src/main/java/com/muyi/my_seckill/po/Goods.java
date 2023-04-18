package com.muyi.my_seckill.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 历川
 * @version 1.0
 * @description 商品类
 * @date 2023/4/18 19:06
 */
@Data
@TableName("goods")
public class Goods {
    //商品id

    private Long id;

    //商品名称

    private String goodsName;

    //商品标签

    private String goodsTitle;

    //商品照片

    private String goodsImg;

    //商品价格

    private BigDecimal goodsPrice;

    //商品库存

    private Integer goodsStock;

    //商品创建日期

    private Date createDate;

    //商品修改日期

    private Date updateDate;

    //商品详情

    private String goodsDetail;
}
