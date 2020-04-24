package com.tth.miaosha.controller;

import com.tth.miaosha.dao.MiaoShaUserDao;
import com.tth.miaosha.domain.User;
import com.tth.miaosha.redis.RedisService;
import com.tth.miaosha.redis.UserKey;
import com.tth.miaosha.result.CodeMsg;
import com.tth.miaosha.result.Result;
import com.tth.miaosha.service.MiaoShaUserService;
import com.tth.miaosha.service.UserService;
import com.tth.miaosha.util.ValidatorUtil;
import com.tth.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

//    @Autowired
//    UserService userService;

    @Autowired
    MiaoShaUserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo){
        log.info(loginVo.toString());
        //参数校验
//        String passInput = loginVo.getPassword();
//        String mobile = loginVo.getMobile();
//        System.out.println("passInput: " + passInput + ", mobile: " + mobile);
//        if(StringUtils.isEmpty(passInput)){
//            return Result.error(CodeMsg.PASSWORD_EMPTY);
//        }
//        if(StringUtils.isEmpty(mobile)){
//            return Result.error(CodeMsg.MOBILE_EMPTY);
//        }
//        if(!ValidatorUtil.isMobile(mobile)){
//            return Result.error(CodeMsg.MOBILE_ERROR);
//        }
        //登录
//        CodeMsg cm = userService.login(loginVo);
        userService.login(response,loginVo);
//        if(cm.getCode() == 0){
//            return Result.success(true);
//        }else{
//            return Result.error(cm);
//        }
        return Result.success(true);
    }

}
