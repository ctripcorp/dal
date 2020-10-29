package com.ctrip.platform.dal.application.utils;

import java.util.Random;

public class RandomGenerator {

    private static Random random = new Random();

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();

    }

    public static int getRandomInt() {
        return random.nextInt();
    }
}