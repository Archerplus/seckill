package com.tth.miaosha.controller;

import com.tth.miaosha.domain.User;
//import com.tth.miaosha.rabbitmq.MQSender;
import com.tth.miaosha.rabbitmq.MQSender;
import com.tth.miaosha.redis.KeyPrefix;
import com.tth.miaosha.redis.RedisService;
import com.tth.miaosha.redis.UserKey;
import com.tth.miaosha.result.Result;
import com.tth.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    @RequestMapping("thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","Archer");
        return "hello";
    }

    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq(){
        sender.send("hello Arher");
        return Result.success("hello,world");
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet(){
        User user = userService.getById(2);
        return Result.success(user);
    }


    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx(){
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){
        User user = redisService.get(UserKey.getById,"1",User.class);
        return Result.success(user);
    }

//    @RequestMapping("/redis/set")
//    @ResponseBody
//        public Result<String> redisSet(){
//        Boolean res = redisService.set(UserKey.getById,"k3","hello Saber");
//        String str = redisService.get(KeyPrefix,"k3",String.class);
//        System.out.println("res: " + res + ", key: k3, value: " + str);
//        return Result.success(str);
//    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user = new User();
        user.setId(1);
        user.setName("11111");
        redisService.set(UserKey.getById,"1",user);
        return Result.success(true);
    }
}
