package com.tth.miaosha.redis;

public interface KeyPrefix {
    public int expireSeconds();

    public String getPrefix();
}
