package com.muyi.my_seckill.controller;

import com.muyi.my_seckill.bo.GoodsBo;
import com.muyi.my_seckill.common.CodeMsg;
import com.muyi.my_seckill.common.Result;
import com.muyi.my_seckill.po.OrderInfo;
import com.muyi.my_seckill.po.User;
import com.muyi.my_seckill.redis.RedisService;
import com.muyi.my_seckill.redis.UserKey;
import com.muyi.my_seckill.service.SeckillGoodsService;
import com.muyi.my_seckill.service.SeckillOrderService;
import com.muyi.my_seckill.util.CookieUtil;
import com.muyi.my_seckill.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yaml.snakeyaml.events.Event;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.swing.text.rtf.RTFEditorKit;

/**
 * @author 历川
 * @version 1.0
 * @description 秒杀订单接口
 * @date 2023/5/8 18:54
 */
@Controller
@RequestMapping("/order")
public class SeckillOrderController {
    @Autowired
    RedisService redisService;
    @Autowired
    SeckillOrderService seckillOrderService;
    @Autowired
    SeckillGoodsService seckillGoodsService;
    @RequestMapping("/datail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, HttpServletRequest request, @RequestParam("orderId") long orderId){
        String token = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName,token,User.class);
        if (user == null){
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }

        OrderInfo orderInfo = seckillOrderService.getOrderInfo(orderId);
        if (orderInfo == null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        Long goodsId = orderInfo.getGoodsId();
        GoodsBo goods = seckillGoodsService.getSeckillGoodsBoByGoodsId(goodsId);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(orderInfo);
        orderDetailVo.setGoods(goods);
        return Result.success(orderDetailVo);
    }
}
