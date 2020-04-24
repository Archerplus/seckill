package com.tth.miaosha;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import java.util.HashMap;
import java.util.Map;

//需要继承SpringBootServletInitializer类，才能打war包
// extends SpringBootServletInitializer
@SpringBootApplication
@MapperScan(basePackages = "com.tth.miaosha.dao")
public class MainApplication{
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class,args);
    }
    //打war包需要重写的方法
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
//        return builder.sources(MainApplication.class);
//    }
}