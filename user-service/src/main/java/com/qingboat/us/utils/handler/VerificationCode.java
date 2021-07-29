package com.qingboat.us.utils.handler;

import java.util.Random;

public class VerificationCode {
    public static String getVerificationCode(){
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }

}
