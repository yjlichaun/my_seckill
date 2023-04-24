package com.muyi.my_seckill.controller;

import com.muyi.my_seckill.bo.GoodsBo;
import com.muyi.my_seckill.common.CodeMsg;
import com.muyi.my_seckill.common.Const;
import com.muyi.my_seckill.common.Result;
import com.muyi.my_seckill.po.User;
import com.muyi.my_seckill.redis.GoodsKey;
import com.muyi.my_seckill.redis.RedisService;
import com.muyi.my_seckill.redis.UserKey;
import com.muyi.my_seckill.service.SeckillGoodsService;
import com.muyi.my_seckill.util.CookieUtil;
import com.muyi.my_seckill.vo.GoodsDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.yaml.snakeyaml.events.Event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.sql.ResultSet;
import java.util.List;

/**
 * @author 历川
 * @version 1.0
 * @description 物品操作接口
 * @date 2023/4/24 19:00
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    RedisService redisService;
    @Autowired
    SeckillGoodsService seckillGoodsService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;
    /***
     * @description 获取商品列表
     * @param model 模型
     * @param request 网页请求体
     * @param response 网页响应体
     * @return java.lang.String
     * @author 历川
     * @date 2023/4/24 20:03
    */
    @RequestMapping("/list")
    @ResponseBody
    public String list(Model model, HttpServletRequest request, HttpServletResponse response) {
        //从redis缓存直接读取
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        List<GoodsBo> goodsList = seckillGoodsService.getSeckillGoodsList();
        model.addAttribute("goodsList",goodsList);
        /*
        这段代码创建了一个新的 Web 上下文对象（IWebContext），并将该对象初始化为包含当前 HTTP 请求相关的信息。
        具体来说，代码中的参数分别是:
        request：HTTP 请求对象，包含客户端发起请求的相关信息。
        response：HTTP 响应对象，用于返回数据给客户端。
        request.getServletContext()：获取 Servlet 上下文对象，包含了关于 ApplicationContext 的信息。
        request.getLocale()：获取客户端的语言环境信息。
        model.asMap()：将 Spring MVC 的 Model 对象转换成一个 Map，以便在模板中使用。
        这些参数被传递给了 WebContext 构造函数，以便构建一个包含所有这些信息的上下文对象，并将其赋值给变量 ctx。这个上下文对象可以在模板渲染过程中被使用，以便让模板访问到相关的请求、响应等信息。
       */
        IWebContext ctx = new WebContext(request,response,
                request.getServletContext(),request.getLocale(),model.asMap());
        //手动渲染 使用 Thymeleaf 模板引擎将名为 "goodsList" 的模板文件渲染成 HTML 字符串，并将结果赋值给变量 html。
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList",ctx);
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsList,"", html ,Const.RedisCacheExtime.GOODS_LIST);
        }
        return html;
    }
    /***
     * @description 获取商品介绍对象
     * @param model mvc模型
     * @param request 请求体
     * @param goodsId 商品id
     * @return com.muyi.my_seckill.common.Result<com.muyi.my_seckill.vo.GoodsDetailVo> 商品介绍类对象
     * @author 历川
     * @date 2023/4/24 20:57
    */
    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(Model model, HttpServletRequest request , @PathVariable("goodsId") long goodsId){
        //获取登录用户信息
        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName,loginToken, User.class);
        //根据物品id获取物品消息
        GoodsBo goods = seckillGoodsService.getSeckillGoodsBoByGoodsId(goodsId);
        if (goods == null) {
            return Result.error(CodeMsg.NO_GOODS);
        }else {
            model.addAttribute("goods",goods);
            long startAt = goods.getStartDate().getTime();
            long endAt = goods.getEndDate().getTime();
            long now = System.currentTimeMillis();

            int SeckillStatus = 0;
            int remainSeconds = 0;
            //秒杀还没开始，倒计时阶段
            if (now < startAt){
                remainSeconds = (int) ((startAt - now) / 1000);
            }else if (now > endAt){
                //秒杀结束
                SeckillStatus = 2;
                remainSeconds = -1;
            }else {
                //秒杀进行中
                SeckillStatus = 1;
            }
            //包装成物品介绍类
            GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
            goodsDetailVo.setGoods(goods);
            goodsDetailVo.setUser(user);
            goodsDetailVo.setMiaoshaStatus(SeckillStatus);
            goodsDetailVo.setRemainSeconds(remainSeconds);
            return Result.success(goodsDetailVo);
        }
    }
    /***
     * @description 获取信息页面
     * @param model mvc模型
     * @param goodsId 商品id
     * @param response 响应对象
     * @param request 请求对象
     * @return java.lang.String
     * @author 历川
     * @date 2023/4/24 20:56
    */
    @RequestMapping("/to_detail/{goodsId}")
    @ResponseBody
    public String toDetail(Model model,@PathVariable("goodsId") long  goodsId,HttpServletResponse response,HttpServletRequest request){
        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName,loginToken,User.class);
        model.addAttribute("user",user);
        //从redis获取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail,""+goodsId,String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }
        GoodsBo goods = seckillGoodsService.getSeckillGoodsBoByGoodsId(goodsId);
        if (goods == null){
            return "页面不存在";
        }else {
            model.addAttribute("goods",goods);
            long startAt = goods.getStartDate().getTime();
            long endAt = goods.getEndDate().getTime();
            long now = System.currentTimeMillis();

            int miaoshaStatus = 0;
            int remainSeconds = 0;
            if(now < startAt ) {//秒杀还没开始，倒计时
                remainSeconds = (int)((startAt - now )/1000);
            }else  if(now > endAt){//秒杀已经结束
                miaoshaStatus = 2;
                remainSeconds = -1;
            }else {//秒杀进行中
                miaoshaStatus = 1;
            }
            model.addAttribute("seckillStatus", miaoshaStatus);
            model.addAttribute("remainSeconds", remainSeconds);
            IWebContext ctx = new WebContext(request,response,
                    request.getServletContext(),request.getLocale(),model.asMap());
            html = thymeleafViewResolver.getTemplateEngine().process("goods_detail",ctx);
            if (!StringUtils.isEmpty(html)){
                redisService.set(GoodsKey.getGoodsDetail,"",html,Const.RedisCacheExtime.GOODS_INFO);
            }
            return html;
        }
    }
}





















