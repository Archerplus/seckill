package com.tth.miaosha.redis;

public class MiaoShaUserKey extends BasePrefix{
    public static final int TOKEN_EXPIRE = 3600*24 * 2;
    private MiaoShaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static MiaoShaUserKey token = new MiaoShaUserKey(TOKEN_EXPIRE, "tk");
    public static MiaoShaUserKey getById = new MiaoShaUserKey(0, "id");
}
