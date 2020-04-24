package com.tth.miaosha.config;

import com.tth.miaosha.domain.MiaoShaUser;
import com.tth.miaosha.service.MiaoShaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    MiaoShaUserService miaoShaUserService;

    //用于判定是否需要处理该参数分解，返回true为需要，并会去调用下面的方法resolveArgument。
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();  //获取参数的类型
        //如果类型是MiaoShaUser才会做下面的处理，如果没有这个参数，就不做这个处理
        return clazz == MiaoShaUser.class;//如果类型为MiaoShaUser,就返回true
    }

    //真正用于处理参数分解的方法，返回的Object就是controller方法上的形参对象。
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return UserContext.getUser();
    }
}

