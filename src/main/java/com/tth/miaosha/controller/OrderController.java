package com.tth.miaosha.controller;

import com.tth.miaosha.domain.MiaoShaUser;
import com.tth.miaosha.domain.OrderInfo;
import com.tth.miaosha.redis.RedisService;
import com.tth.miaosha.result.CodeMsg;
import com.tth.miaosha.result.Result;
import com.tth.miaosha.service.GoodsService;
import com.tth.miaosha.service.MiaoShaUserService;
import com.tth.miaosha.service.OrderService;
import com.tth.miaosha.vo.GoodsVo;
import com.tth.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    MiaoShaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, MiaoShaUser user,
                                      @RequestParam("orderId")long orderId){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(order == null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }
}
