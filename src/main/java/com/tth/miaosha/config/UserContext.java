package com.tth.miaosha.config;

import com.tth.miaosha.domain.MiaoShaUser;

public class UserContext {
    private static ThreadLocal<MiaoShaUser> userHolder = new ThreadLocal<>();

    public static void setUser(MiaoShaUser user){
        userHolder.set(user);
    }

    public static MiaoShaUser getUser(){
        return (MiaoShaUser)userHolder.get();
    }

}
