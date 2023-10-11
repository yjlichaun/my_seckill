package com.muyi.my_seckill.controller;

import com.muyi.my_seckill.annotations.AccessLimit;
import com.muyi.my_seckill.bo.GoodsBo;
import com.muyi.my_seckill.common.CodeMsg;
import com.muyi.my_seckill.common.Const;
import com.muyi.my_seckill.common.Result;
import com.muyi.my_seckill.mq.MQReceiver;
import com.muyi.my_seckill.mq.MQSender;
import com.muyi.my_seckill.mq.SeckillMessage;
import com.muyi.my_seckill.po.OrderInfo;
import com.muyi.my_seckill.po.SeckillOrder;
import com.muyi.my_seckill.po.User;
import com.muyi.my_seckill.redis.GoodsKey;
import com.muyi.my_seckill.redis.RedisService;
import com.muyi.my_seckill.redis.UserKey;
import com.muyi.my_seckill.service.SeckillGoodsService;
import com.muyi.my_seckill.service.SeckillOrderService;
import com.muyi.my_seckill.util.CookieUtil;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/4/25 11:57
 */
@Controller
@RequestMapping("seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    RedisService redisService;
    @Autowired
    SeckillOrderService seckillOrderService;
    @Autowired
    MQSender mqSender;
    @Autowired
    SeckillGoodsService seckillGoodsService;
    /**
     * 如果是集群情况下，需要达到一定量此缓存才能起到重大作用
     */
    private final HashMap<Long,Boolean> localOverMap = new HashMap<>();

    @PostMapping("/{path}/seckill")
    @ResponseBody
    public Result<Integer> list(Model model
                                , @RequestParam("goodsId") long goodsId , @PathVariable("path") String path, HttpServletRequest request){
        //验证登录
        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName,loginToken,User.class);
        if (user == null){
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        //验证path
        boolean check = seckillOrderService.checkPath(user,goodsId,path);
        if (!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记。减少redis访问,判断秒杀是否结束
        boolean over = localOverMap.get(goodsId);
        if (over){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //预减库存
        Long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, String.valueOf(goodsId));
        if (stock < 0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否秒杀到
        SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdAndGoodsId(user.getId(),goodsId);
        if (order != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setUser(user);
        seckillMessage.setGoodsId(goodsId);
        mqSender.sendSeckillMessage(seckillMessage);
        return Result.success(0);
    }
    @RequestMapping("/seckill2")
    public String list2(Model model ,@RequestParam("goodsId") long goodsId,HttpServletRequest request){
        String token = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName,token,User.class);
        if (user == null){
            return "login";
        }
        //判断库存
        GoodsBo goods = seckillGoodsService.getSeckillGoodsBoByGoodsId(goodsId);
        int stock = goods.getGoodsStock();
        if (stock < 0){
            model.addAttribute("errmsg",CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //判断是否已经秒杀到
        SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (order != null){
            model.addAttribute("errmsg",CodeMsg.REPEATE_MIAOSHA.getMsg());
            return  "miaosha_fail";
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = seckillOrderService.insert(user,goods);
        model.addAttribute("orderinfo",orderInfo);
        model.addAttribute("goods",goods);
        return "order_detail";
    }
    /**
     * 将库存初始化到本地缓存及redis缓存，原则上次块应该在创建秒杀活动时候触发的（为了演示，此项目没有创建活动逻辑，所有放在启动项目时候放进内存）
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsBo> goodsList = seckillGoodsService.getSeckillGoodsList();
        if (goodsList == null) {
            return;
        }
        for (GoodsBo goods : goodsList) {
            redisService.set(GoodsKey.getSeckillGoodsStock, String.valueOf(goods.getId()), goods.getStockCount(), Const.RedisCacheExtime.GOODS_LIST);
            localOverMap.put(goods.getId(), false);
        }
    }
    /**
     * 客户端轮询查询是否下单成功
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @GetMapping("/result")
    @ResponseBody
    public Result<Long> seckillResult(@RequestParam("goodsId") long goodsId,HttpServletRequest request){
        String token = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName,token,User.class);
        if (user == null){
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        long result = seckillOrderService.getSeckillResult((long) user.getId(),goodsId);
        return Result.success(result);
    }
    @AccessLimit(seconds = 5,maxCount = 5,needLogin = true)
    @GetMapping("/path")
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request,User user,@RequestParam("goodsId") long goodsId){
        String token = CookieUtil.readLoginToken(request);
        user = redisService.get(UserKey.getByName,token,User.class);
        if (user == null){
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        String path = seckillOrderService.createMiaoshaPath(user,goodsId);
        return Result.success(path);
    }
}
