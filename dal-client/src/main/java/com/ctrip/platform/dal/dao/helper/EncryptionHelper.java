package com.ctrip.platform.dal.dao.helper;

import java.math.BigInteger;
import java.security.MessageDigest;

public class EncryptionHelper {
    private static final String MD5 = "MD5";
    private static final int SIGNUM = 1;
    private static final int RADIX = 16;
    private static final int BEGIN_INDEX = 0;
    private static final int END_INDEX = 8;

    public static String getCRC(String value) {
        if (value == null || value.isEmpty())
            return "";

        try {
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(value.getBytes());

            BigInteger integer = new BigInteger(SIGNUM, digest.digest());
            String md5 = integer.toString(RADIX);
            return md5.substring(BEGIN_INDEX, END_INDEX);
        } catch (Exception e) {
            return "";
        }
    }

}
