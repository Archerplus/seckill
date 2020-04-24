package com.tth.miaosha.service;

import com.tth.miaosha.dao.MiaoShaUserDao;
import com.tth.miaosha.domain.MiaoShaUser;
import com.tth.miaosha.exception.GlobalException;
import com.tth.miaosha.redis.MiaoShaUserKey;
import com.tth.miaosha.redis.RedisService;
import com.tth.miaosha.result.CodeMsg;
import com.tth.miaosha.util.MD5Util;
import com.tth.miaosha.util.UUIDUtil;
import com.tth.miaosha.vo.LoginVo;
import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class MiaoShaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoShaUserDao miaoShaUserDao;

    @Autowired
    RedisService redisService;

    //对象缓存
    public MiaoShaUser getById(long id){
        //取缓存
        MiaoShaUser user = redisService.get(MiaoShaUserKey.getById,id + "",MiaoShaUser.class);
        if(user != null){
            return user;
        }
        //取数据库
        user = miaoShaUserDao.getById(id);
        if(user != null){
            redisService.set(MiaoShaUserKey.getById,id + "",user);
        }
        return user;
    }

    public boolean updatePassword(String token,long id,String passwordNew){
        //取user对象
        MiaoShaUser user = getById(id);
        if(user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        MiaoShaUser toBeUpdate = new MiaoShaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(passwordNew,user.getSalt()));
        miaoShaUserDao.update(toBeUpdate);
        //处理缓存
        redisService.delete(MiaoShaUserKey.getById,""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoShaUserKey.token, token,user);
        return true;
    }

    public boolean login(HttpServletResponse response,LoginVo loginVo) {
        if(loginVo == null){
//            return CodeMsg.SERVER_ERROR;
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MiaoShaUser user = getById(Long.parseLong(mobile));
        if(user == null){
//            return CodeMsg.MOBILE_NOT_EXIST;
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        System.out.println("dbPass: " + dbPass + ", saltDB: " + saltDB);
        String calcPass = MD5Util.formPassToDBPass(formPass,saltDB);
        System.out.println("calcPass: " + calcPass + ", dbPass: " + dbPass);
        if(!calcPass.equals(dbPass)){
//            return CodeMsg.PASSWORD_ERROR;
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //登录成功之后生成一个cookie
        //将token写到cookie当中，然后传递给客户端
        //将私人信息存放到第三方的缓存当中
        String token = UUIDUtil.uuid();
        addCookie(response,token,user);
        return true;
    }

    public MiaoShaUser getByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoShaUser user = redisService.get(MiaoShaUserKey.token,token,MiaoShaUser.class);
        //延长有效期
        //重新设置缓存里面的过期时间
        if(user != null){
            addCookie(response,token,user);
        }
        return user;
    }
    private void addCookie(HttpServletResponse response,String token,MiaoShaUser user){
        //token只需要第一次生成，后面的就只需要更新就行
        redisService.set(MiaoShaUserKey.token,token,user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoShaUserKey.token.expireSeconds());//设置cookie的有效期
        cookie.setPath("/");//设置网站的根目录
        response.addCookie(cookie);//将cookie添加到客户端中去
    }
}
