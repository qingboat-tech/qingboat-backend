package com.qingboat.base.utils;

import com.qingboat.base.exception.BaseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class BASE64Util {
    private final static Base64.Decoder decoder = Base64.getDecoder();
    private final static Base64.Encoder encoder = Base64.getEncoder();

    public static String encode(String str){
        if (str == null || str.length() ==0){
            return str;
        }
        String encodedText = encoder.encodeToString(str.getBytes(StandardCharsets.UTF_8));
        return encodedText;
    }

    public static String decode(String str){
        if (str == null || str.length() ==0){
            return str;
        }
        try {
            byte[] decodeByte = decoder.decode(str);
            return new String(decodeByte);

        }catch (Exception e){
            throw new BaseException(500,"非法加密参数");
        }

    }

    public static void main(String[] args){

        String a = "609e559e8c056234be2fb50d#61";

        String encodedText = BASE64Util.encode(a);
        System.err.println(encodedText);
        String b = BASE64Util.decode(encodedText+"LLLKK");
        System.err.println(b);

    }
}
