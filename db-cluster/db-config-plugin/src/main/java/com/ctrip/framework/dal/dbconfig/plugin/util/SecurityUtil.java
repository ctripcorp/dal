package com.ctrip.framework.dal.dbconfig.plugin.util;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import qunar.codec.Base64;

import javax.crypto.Cipher;
import java.security.Key;
import java.util.Iterator;
import java.util.List;


public class SecurityUtil {
    private static final char STX = (char) 2; // start text
    private static final char ETX = (char) 3; // end text
    private static final char FF = '\r';
    private static final char CR = '\n';

    private static final Splitter SPLITTER = Splitter.on(CR).trimResults();

    private static ThreadLocal<Cipher> CIPHER = new ThreadLocal<Cipher>() {
        @Override
        protected Cipher initialValue() {
            try {
                return Cipher.getInstance("RSA");
            } catch (Exception e) {
                throw new RuntimeException("get RSA Instance Fail");
            }
        }
    };

    //decode token
    public static List<String> decode(String token, Key privateKey) {
        List<String> list = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(token)){
	        try {
	            Iterator<String> i = SPLITTER.split(decrypt(token, privateKey)).iterator();
	            while(i.hasNext()){
	            	list.add(i.next());
	            }
	        } catch (Exception e) {
	            throw new RuntimeException("解密应用配置失败, token=" + token, e);
	        }
        }
        return list;
    }


    private static String decrypt(String secret, Key privateKey) throws Exception {
        byte[] bytes = decryptBASE64(secret.replace(STX, FF).replace(ETX, CR));

        Cipher cipher = CIPHER.get();
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return new String(cipher.doFinal(bytes), "UTF-8");
    }

    private static byte[] decryptBASE64(String key) throws Exception {
        return Base64.decode(key);
    }

}
