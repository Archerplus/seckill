package com.tth.miaosha.controller;

import com.tth.miaosha.domain.MiaoShaUser;
import com.tth.miaosha.redis.GoodsKey;
import com.tth.miaosha.redis.RedisService;
import com.tth.miaosha.result.Result;
import com.tth.miaosha.service.GoodsService;
import com.tth.miaosha.service.MiaoShaUserService;
import com.tth.miaosha.vo.GoodsDetailVo;
import com.tth.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoShaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    //页面缓存的有效期比较短
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String List(HttpServletRequest request,HttpServletResponse response,Model model,
                       MiaoShaUser user){
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if(!StringUtils.isEmpty(html)) {
            System.out.println("取缓存");
            return html;
        }
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("user",user);
        model.addAttribute("goodsList", goodsList);

        SpringWebContext ctx = new SpringWebContext(request,response,
                request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
        //手动渲染
        System.out.println("手动渲染");
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }

    //produces代表返回的是一个html格式
//    @RequestMapping(value = "/to_detail2/{goodsId}",produces = "text/html")
//    @ResponseBody
//    public String detail2(HttpServletRequest request,HttpServletResponse response,Model model, MiaoShaUser user, @PathVariable("goodsId")long goodsId){
//        //snowflake  自增id
//        model.addAttribute("user",user);
//
//        //取缓存
//        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
//        if(!StringUtils.isEmpty(html)) {
//            System.out.println("goods_detail: 取缓存");
//            return html;
//        }
//
//        //手动渲染
//        System.out.println("goods_detail: 手动渲染");
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//        model.addAttribute("goods",goods);
//        long startAt = goods.getStartDate().getTime();
//        long endAt = goods.getEndDate().getTime();
//        long now = System.currentTimeMillis();
//        int miaoshaStatus = 0;
//        int remainSeconds = 0;
//        if(now < startAt){//秒杀还没有开始，倒计时
//            miaoshaStatus = 0;
//            remainSeconds = (int)((startAt - now) / 1000);
//        }else if(now > endAt){//秒杀已经结束
//            miaoshaStatus = 2;
//            remainSeconds = -1;
//        }else{//秒杀正在进行中
//            miaoshaStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("miaoshaStatus",miaoshaStatus);//秒杀状态
//        model.addAttribute("remainSeconds",remainSeconds);//还有多久开始
////        return "goods_detail";
//
//        SpringWebContext ctx = new SpringWebContext(request,response,
//                request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
//        //手动渲染
//        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
//        if(!StringUtils.isEmpty(html)) {
//            redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
//        }
//        return html;
//    }



    //produces代表返回的是一个html格式
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoShaUser user, @PathVariable("goodsId")long goodsId){
        //snowflake  自增id
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;

        if(now < startAt){//秒杀还没有开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now) / 1000);
        }else if(now > endAt){//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else{//秒杀正在进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setMiaoshaStatus(miaoshaStatus);
        vo.setRemainSeconds(remainSeconds);
        return Result.success(vo);
    }
}
