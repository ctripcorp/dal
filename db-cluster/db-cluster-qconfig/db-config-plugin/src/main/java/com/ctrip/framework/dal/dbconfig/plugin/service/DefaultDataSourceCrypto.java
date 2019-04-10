package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.entity.KeyInfo;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;


public class DefaultDataSourceCrypto implements DataSourceCrypto {
    private static class DefaultDataSourceCryptoSingletonHolder {
        private static final DefaultDataSourceCrypto instance = new DefaultDataSourceCrypto();
    }

    //=== Expose method ===
    public static DefaultDataSourceCrypto getInstance(){
        return DefaultDataSourceCryptoSingletonHolder.instance;
    }

    //=== Private Constructor ===
    private DefaultDataSourceCrypto(){ }

    private Map<String, Cipher> m_encryptCipherMap = new HashMap<String, Cipher>();     //sslCode   ->  encryptCipher
    private Map<String, Cipher> m_decryptCipherMap = new HashMap<String, Cipher>();     //sslCode   ->  decryptCipher


    @Override
    public String encrypt(String content, KeyInfo key) throws Exception {
        if (content == null || content.length() == 0) {
            Cat.logEvent("Crypto.Encrypt", "InvalidContent", Event.SUCCESS, "content is null or empty");
            throw new IllegalArgumentException("content is null or empty");
        }

        String sslCode = key.getSslCode();
        if (!m_encryptCipherMap.containsKey(sslCode)) {
            initialize(key);
        }

        if (key.getKey() == null || key.getKey().length() == 0) {
            Cat.logEvent("Crypto.Encrypt", "InvalidKey", Event.SUCCESS, "key is null or empty");
        }

        try {
            byte[] byteContent = content.getBytes("utf-8");
            byte[] result = m_encryptCipherMap.get(sslCode).doFinal(byteContent);

            return parseByte2HexStr(result);
        } catch (Exception e) {
            Cat.logError(e);
            throw e;
        }
    }

    @Override
    public String decrypt(String content, KeyInfo key) throws Exception {
        if (content == null || content.length() == 0) {
            Cat.logEvent("Crypto.Decrypt", "InvalidContent", Event.SUCCESS, "content is null or empty");
            throw new IllegalArgumentException("content is null or empty");
        }

        String sslCode = key.getSslCode();
        if (!m_encryptCipherMap.containsKey(sslCode)) {
            initialize(key);
        }

        if (key.getKey() == null || key.getKey().length() == 0) {
            Cat.logEvent("Crypto.Decrypt", "InvalidKey", Event.SUCCESS, "key is null or empty");
        }

        try {
            byte[] byteContent = parseHexStr2Byte(content);
            byte[] result = m_decryptCipherMap.get(sslCode).doFinal(byteContent);

            return new String(result);
        } catch (Exception e) {
            Cat.logError(e);
            throw e;
        }
    }

    private String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    private byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    private void initialize(KeyInfo keyInfo) {
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "DefaultDataSourceCrypto");
        try {
            t.addData("keyInfo.m_sslCode", keyInfo.getSslCode());

            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(keyInfo.getKey().getBytes());
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");

            Cipher encryptCipher = Cipher.getInstance("AES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            m_encryptCipherMap.put(keyInfo.getSslCode(), encryptCipher);

            Cipher decryptCipher = Cipher.getInstance("AES");
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
            m_decryptCipherMap.put(keyInfo.getSslCode(), decryptCipher);

            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logEvent("Crypto.Initialize", "Failed");
            Cat.logError(e);
        } finally {
            t.complete();
        }
    }


}
