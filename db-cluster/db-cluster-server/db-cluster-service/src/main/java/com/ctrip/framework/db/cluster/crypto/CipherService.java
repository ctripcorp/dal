package com.ctrip.framework.db.cluster.crypto;

import com.dianping.cat.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * Created by shenjie on 2019/3/27.
 */
@Service
public class CipherService {

    @Autowired
    private SecretService secretService;
    private SecretKeySpec secretKeySpec;

    private ThreadLocal<Cipher> encryptCipher = new ThreadLocal<Cipher>() {
        @Override
        protected Cipher initialValue() {
            return createCipher(Cipher.ENCRYPT_MODE);
        }
    };
    private ThreadLocal<Cipher> decryptCipher = new ThreadLocal<Cipher>() {
        @Override
        protected Cipher initialValue() {
            return createCipher(Cipher.DECRYPT_MODE);
        }
    };

    @PostConstruct
    private void init() throws Exception {
        String signature = secretService.getSignature();
        buildSecretKeySpec(signature);
        validate();
    }

    private void buildSecretKeySpec(String signature) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(signature.getBytes());
        keyGenerator.init(128, random);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
    }

    public String encrypt(String content) throws Exception {
        Cipher cipher = encryptCipher.get();
        try {
            byte[] byteContent = content.getBytes("utf-8");
            byte[] result = cipher.doFinal(byteContent);

            return parseByte2HexStr(result);
        } catch (Exception e) {
            Cat.logError(e);
            throw e;
        }
    }

    public String decrypt(String content) throws Exception {
        Cipher cipher = decryptCipher.get();
        try {
            byte[] byteContent = parseHexStr2Byte(content);
            byte[] result = cipher.doFinal(byteContent);

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

    private Cipher createCipher(int mode) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(mode, secretKeySpec);
        } catch (Exception e) {
            Cat.logError("createCipherException", e);
        }

        return cipher;
    }

    //validate
    private void validate() throws Exception {
        String content = "Hello Key!";
        String encryptedContent = encrypt(content);
        String decryptedContent = decrypt(encryptedContent);

        if (!decryptedContent.equals(content)) {
            throw new SecretKeyServiceException("加解密验证失败.");
        }
    }

}
