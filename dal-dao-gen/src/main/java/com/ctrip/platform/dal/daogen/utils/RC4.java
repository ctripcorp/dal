package com.ctrip.platform.dal.daogen.utils;

import org.apache.commons.codec.binary.Base64;

public class RC4 {
    public static String decrypt(String datasource) {
        if (datasource == null || datasource.length() == 0) {
            return "";
        }
        byte[] sources = Base64.decodeBase64(datasource);
        int datalen = sources.length;
        int keyLen = (int) sources[0];
        int len = datalen - keyLen - 1;
        byte[] datas = new byte[len];
        int offset = datalen - 1;
        int i = 0;
        int j = 0;
        byte t;
        for (int o = 0; o < len; o++) {
            i = (i + 1) % keyLen;
            j = (j + sources[offset - i]) % keyLen;
            t = sources[offset - i];
            sources[offset - i] = sources[offset - j];
            sources[offset - j] = t;
            datas[o] = (byte) (sources[o + 1] ^ sources[offset - ((sources[offset - i] + sources[offset - j]) % keyLen)]);
        }
        return new String(datas);
    }
}
