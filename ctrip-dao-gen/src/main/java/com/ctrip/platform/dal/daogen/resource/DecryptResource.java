package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.decrypt.DecryptInfo;
import org.apache.commons.codec.binary.Base64;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.net.URLDecoder;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@Resource
@Singleton
@Path("decryption")
public class DecryptResource {
    private static final String secretKey = "dalctripcn";
    private static final String UTF8 = "UTF-8";
    private static final String DES = "DES";
    private static final String CBC = "DES/CBC/PKCS5Padding";

    private static final String splitterAnd = "&";
    private static final String equal = "=";
    private static final String doubleEqual = "==";
    private static final String tripleEqual = "===";
    private static final String and = "&";

    private Cipher decryptCipher = null;
    private static final Object decryptCipherLock = new Object();

    private Cipher decryptCipherNet = null;
    private static final Object decryptCipherNetLock = new Object();

    private Cipher getDecryptCipher() {
        if (decryptCipher == null) {
            synchronized (decryptCipherLock) {
                if (decryptCipher == null) {
                    decryptCipher = initDecryptCipher();
                }
            }
        }

        return decryptCipher;
    }

    private Cipher getDecryptCipherNet() {
        if (decryptCipherNet == null) {
            synchronized (decryptCipherNetLock) {
                if (decryptCipherNet == null) {
                    decryptCipherNet = initDecryptCipherNet();
                }
            }
        }

        return decryptCipherNet;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("decrypt")
    public DecryptInfo getDecryptInfo(@QueryParam("encrypt") String encrypt) {
        DecryptInfo result = new DecryptInfo();
        try {
            String decrypt = desDecrypt(encrypt);
            result.setDecryptMsg(decrypt);
            result.setErrorMsg("");
        } catch (Throwable e) {
            try {
                String decrypt2 = desDecrypt(URLDecoder.decode(encrypt, "UTF-8"));
                result.setDecryptMsg(decrypt2);
                result.setErrorMsg("");
            } catch (Throwable e1) {
                result.setErrorMsg(e.getMessage());
            }
        }

        return result;
    }

    private Cipher initDecryptCipher() {
        Cipher cipher = null;
        try {
            byte[] bytes = secretKey.substring(0, 8).getBytes("UTF-8");
            Key key = new javax.crypto.spec.SecretKeySpec(bytes, "DES");
            cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Throwable e) {
        }

        return cipher;
    }

    private String desDecrypt(String encryptString) throws Exception {
        if (encryptString == null || encryptString.isEmpty())
            return "";

        try {
            Cipher cipher = getDecryptCipher();
            byte[] bytes = Base64.decodeBase64(encryptString.getBytes());
            return new String(cipher.doFinal(bytes));
        } catch (Throwable e) {
            throw e;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("decryptNetClog")
    public DecryptInfo getNetClogDecryptInfo(@QueryParam("encrypt") String encrypt) {
        DecryptInfo result = new DecryptInfo();
        try {
            String decrypt = desDecryptNetClog(encrypt);
            result.setDecryptMsg(decrypt);
            result.setErrorMsg("");
        } catch (Throwable e) {
            try {
                String decrypt2 = desDecryptNetClog(URLDecoder.decode(encrypt, UTF8));
                result.setDecryptMsg(decrypt2);
                result.setErrorMsg("");
            } catch (Throwable e1) {
                result.setErrorMsg(e.getMessage());
            }
        }

        return result;
    }

    private Cipher initDecryptCipherNet() {
        Cipher cipher = null;
        try {
            byte[] keyBytes = secretKey.substring(0, 8).getBytes(UTF8);
            Key key = new javax.crypto.spec.SecretKeySpec(keyBytes, DES);
            cipher = Cipher.getInstance(CBC);
            byte[] IV = keyBytes;
            IvParameterSpec iv2 = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, key, iv2);
        } catch (Throwable e) {
        }

        return cipher;
    }

    private String desDecryptNetClog(String encryptString) throws Exception {
        if (encryptString == null || encryptString.isEmpty())
            return "";

        try {
            Cipher cipher = getDecryptCipherNet();
            byte[] bytes = Base64.decodeBase64(encryptString.getBytes());
            return new String(cipher.doFinal(bytes));
        } catch (Throwable e) {
            throw e;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("decryptNetCat")
    public DecryptInfo getNetCatDecryptInfo(@QueryParam("encrypt") String encrypt) {
        DecryptInfo result = new DecryptInfo();
        try {
            String decrypt = desDecryptNetCat(encrypt);
            result.setDecryptMsg(decrypt);
            result.setErrorMsg("");
        } catch (Throwable e) {
            try {
                String decrypt2 = desDecryptNetCat(URLDecoder.decode(encrypt, UTF8));
                result.setDecryptMsg(decrypt2);
                result.setErrorMsg("");
            } catch (Throwable e1) {
                result.setErrorMsg(e.getMessage());
            }
        }

        return result;
    }

    private String desDecryptNetCat(String encryptString) throws Exception {
        if (encryptString == null || encryptString.isEmpty())
            return "";

        List<String> parameters = new ArrayList<>();
        String[] array = encryptString.split(splitterAnd);
        if (array != null && array.length > 0) {
            for (String item : array) {
                List<String> parameter = new ArrayList<>();
                if (item.indexOf(tripleEqual) > -1) {
                    int index = item.indexOf(tripleEqual);
                    parameter.add(desDecryptNetClog(item.substring(0, index).concat(doubleEqual)));
                    parameter.add(desDecryptNetClog(item.substring(index + tripleEqual.length())));
                } else if (item.indexOf(doubleEqual) > -1) {
                    int index = item.indexOf(doubleEqual);
                    parameter.add(desDecryptNetClog(item.substring(0, index).concat(equal)));
                    parameter.add(desDecryptNetClog(item.substring(index + doubleEqual.length())));
                } else {
                    int index = item.indexOf(equal);
                    parameter.add(desDecryptNetClog(item.substring(0, index)));
                    parameter.add(desDecryptNetClog(item.substring(index + equal.length())));
                }

                parameters.add(String.join(equal, parameter));
            }
        }

        String result = "";
        if (parameters.size() > 0) {
            result = String.join(and, parameters);
        }

        return result;
    }

}
