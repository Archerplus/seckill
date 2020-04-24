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
        userService.login(response,loginVo);
        return Result.success(true);
    }

}
