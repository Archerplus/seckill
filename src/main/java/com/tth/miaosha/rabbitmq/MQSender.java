package com.tth.miaosha.rabbitmq;

import com.tth.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    public void send(Object message){
        String msg = RedisService.beanToString(message);
        System.out.println("send message: " + msg);
        if(amqpTemplate == null){
            System.out.println("ampq is null");
        }else{
            System.out.println("amqp is not null");
        }
        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
    }

    public void sendMiaoshaMessage(MiaoshaMessage mm) {
        String msg = RedisService.beanToString(mm);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,msg);
    }

}
