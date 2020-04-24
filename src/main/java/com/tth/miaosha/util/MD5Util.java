package com.tth.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    public static String md5(String src){
        //进行Md5加密处理
     return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";

    public static String inputPassToFormPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }
    public static String formPassToDBPass(String formPass,String salt){
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String input,String saltDB){
        String formPass = inputPassToFormPass(input);
        String DBPass = formPassToDBPass(formPass,saltDB);
        return DBPass;
    }

    public static void main(String[] args) {
//        System.out.println(inputPassToFormPass("123456"));
//        System.out.println(formPassToDBPass(inputPassToFormPass("12345"),"1a2b3c4d"));
//        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));
    }
}
