package com.tth.miaosha.rabbitmq;

import com.tth.miaosha.domain.MiaoShaUser;
import com.tth.miaosha.domain.MiaoshaOrder;
import com.tth.miaosha.redis.RedisService;
import com.tth.miaosha.result.CodeMsg;
import com.tth.miaosha.result.Result;
import com.tth.miaosha.service.GoodsService;
import com.tth.miaosha.service.MiaoShaUserService;
import com.tth.miaosha.service.MiaoshaService;
import com.tth.miaosha.service.OrderService;
import com.tth.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message){
        System.out.println("receive message: " + message);
        MiaoshaMessage mm = RedisService.stringToBean(message,MiaoshaMessage.class);
        MiaoShaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock < 0){
            return ;
        }
        //判断是否秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null){
            return ;
        }
        //减库存，下订单，写入秒杀订单
        miaoshaService.miaosha(user,goods);

    }
}
