package com.tth.miaosha.controller;

import com.tth.miaosha.domain.MiaoShaUser;
import com.tth.miaosha.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoShaUser> info(Model model,MiaoShaUser user) {
        return Result.success(user);
    }
}
