package com.tth.miaosha.redis;

import com.alibaba.fastjson.JSON;
import com.tth.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;


//    public <T> T get(String key,Class<T> clazz){
//        Jedis jedis = null;
//        try{
//            jedis = jedisPool.getResource();
//            String str = jedis.get(key);
//            System.out.println("key: " + key + ", value: " + str);
//            T t = stringToBean(str,clazz);
//            return t;
//        }finally {
//            returnToPool(jedis);
//        }
//    }

//    public <T> boolean set(String key,T value){
//        Jedis jedis = null;
//        try{
//            jedis = jedisPool.getResource();
//            String str = beanToString(value);
//            if(str == null || str.length() <= 0){
//                return false;
//            }
//            jedis.set(key,str);
//            return true;
//        }finally {
//            returnToPool(jedis);
//        }
//    }


    //设置对象
    public <T> boolean set(KeyPrefix prefix,String key,T value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if(str == null || str.length() <= 0){
                return false;
            }
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            if(seconds <= 0){//为永不过期的
                System.out.println("设置为永不过期");
                System.out.println("set: key: " + realKey + ", value: " + str);
                jedis.set(realKey,str);
            }else{
                System.out.println("过一段时间就过期");
                System.out.println("set: key: " + realKey + ", value: " + str);
//                jedis.set(realKey,str);
                jedis.setex(realKey,seconds,str);  //setex = set + expire
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    public static <T> String beanToString(T value) {

        if(value == null){
            return null;
        }
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class){
            return "" + value;
        }else if(clazz == String.class){
            return (String)value;
        }else if(clazz == long.class || clazz == Long.class){
            return "" + value;
        }else{
            return JSON.toJSONString(value);
        }
    }

    public static <T> T stringToBean(String str,Class<T> clazz) {
        if(str == null || str.length() <= 0 || clazz == null){
            return null;
        }
        if(clazz == int.class || clazz == Integer.class){
            return (T)Integer.valueOf(str);
        }else if(clazz == String.class){
            return (T)str;
        }else if(clazz == long.class || clazz == Long.class){
            return (T)Long.valueOf(str);
        }else{
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }
    }

    public void returnToPool(Jedis jedis) {
        if(jedis != null){
            jedis.close();
        }
    }

    //获取单个对象
    public <T> T get(KeyPrefix prefix,String key,Class<T> clazz){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            String str = jedis.get(realKey);
            System.out.println("get: realKey: " + realKey + ", value: " + str);
            T t = stringToBean(str,clazz);
            return t;
        }finally {
            returnToPool(jedis);
        }
    }

//    判断是否存在
    public <T> boolean exists(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

//    增加值
    public <T> Long incr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

//减少值
    public <T> Long decr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 删除
     */
    public boolean delete(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            long res = jedis.del(realKey);
            return res > 0;
        }finally {
            returnToPool(jedis);
        }
    }
}
