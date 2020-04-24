package com.tth.miaosha.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class demo {

    public static void main(String[] args) {
        MQSender mqSender = new MQSender();
        mqSender.send("hello Archer");
    }
}
