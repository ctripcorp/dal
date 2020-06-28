package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.decrypt.DecryptInfo;
import com.ctrip.platform.dal.daogen.decrypt.PasswordDeriveBytes;
import com.ctrip.platform.dal.sql.logging.CommonUtil;
import org.apache.commons.codec.binary.Base64;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URLDecoder;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@Resource
@Singleton
@Path("decryption")
public class DecryptResource {
    private static final String secretKey = "dalctripcn"; // fltonline dalctripcn
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

    private Cipher decryptCipherNetPDB = null;
    private static final Object decryptCipherNetPDBLock = new Object();

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

    private Cipher getDecryptCipherNetPDB() {
        if (decryptCipherNetPDB == null) {
            synchronized (decryptCipherNetPDBLock) {
                if (decryptCipherNetPDB == null) {
                    decryptCipherNetPDB = initDecryptCipherNetPDB();
                }
            }
        }

        return decryptCipherNetPDB;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("decrypt")
    public DecryptInfo getDecryptInfo(@FormParam("encrypt") String encryptString) {
        DecryptInfo result = new DecryptInfo();
        String decrypt = null;
        encryptString = encryptString.trim().replace("\n", "").replace("\r", "");

        try {
            // Java Clog,CAT
            decrypt = decryptJavaClogOrCat(encryptString);
            if (decrypt == null || decrypt.isEmpty()) {
                decrypt = decryptJavaClogOrCat(URLDecoder.decode(encryptString, UTF8));
            }

            // Java ES
            if (decrypt == null || decrypt.isEmpty()) {
                decrypt = decryptJavaES(encryptString);
            }
            if (decrypt == null || decrypt.isEmpty()) {
                decrypt = decryptJavaES(URLDecoder.decode(encryptString, UTF8));
            }

            result.setDecryptMsg(decrypt);
            result.setErrorMsg("");
        } catch (Throwable e) {
            result.setErrorMsg(e.getMessage());
        }

        return result;
    }

    private Cipher initDecryptCipher() {
        Cipher cipher = null;
        try {
            byte[] bytes = secretKey.substring(0, 8).getBytes(UTF8);
            Key key = new javax.crypto.spec.SecretKeySpec(bytes, DES);
            cipher = Cipher.getInstance(DES);
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Throwable e) {
        }

        return cipher;
    }

    private String decryptJavaClogOrCat(String encryptString) throws Exception {
        if (encryptString == null || encryptString.isEmpty())
            return null;

        try {
            Cipher cipher = getDecryptCipher();
            byte[] bytes = Base64.decodeBase64(encryptString.getBytes());
            return new String(cipher.doFinal(bytes), UTF8);
        } catch (Throwable e) {
            return null;
        }
    }

    private String decryptJavaES(String encryptString) throws Exception {
        if (encryptString == null || encryptString.isEmpty())
            return null;

        try {
            return CommonUtil.desDecrypt(encryptString);
        } catch (Throwable e) {
            return null;
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("decryptNet")
    public DecryptInfo getNetDecryptInfo(@FormParam("encrypt") String encryptString) {
        DecryptInfo result = new DecryptInfo();
        String decrypt = null;
        encryptString = encryptString.trim().replace("\n", "").replace("\r", "");
        Boolean isCat = isCatParameter(encryptString);

        try {
            // .Net Clog & CAT
            if (isCat) {
                decrypt = decryptNetCat(encryptString);
                if (decrypt == null || decrypt.isEmpty()) {
                    decrypt = decryptNetCat(URLDecoder.decode(encryptString, UTF8));
                }
            } else {
                decrypt = decryptNetClog(encryptString);
                if (decrypt == null || decrypt.isEmpty()) {
                    decrypt = decryptNetClog(URLDecoder.decode(encryptString, UTF8));
                }
            }

            // .Net ES
            if (decrypt == null || decrypt.isEmpty()) {
                decrypt = decryptNetES(encryptString);
            }

            result.setDecryptMsg(decrypt);
            result.setErrorMsg("");
        } catch (Throwable e) {
            result.setErrorMsg(e.getMessage());
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

    private Cipher initDecryptCipherNetPDB() {
        Cipher cipher = null;
        try {
            PasswordDeriveBytes db = new PasswordDeriveBytes(secretKey, null);
            byte[] bytes = db.GetBytes(8);
            byte[] bs = {,};
            Key key = new javax.crypto.spec.SecretKeySpec(bytes, DES);
            cipher = Cipher.getInstance(CBC);
            byte[] IV = bytes;
            IvParameterSpec iv2 = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, key, iv2);
        } catch (Throwable e) {
            String str = e.getMessage();
        }

        return cipher;
    }

    private Boolean isCatParameter(String encryptString) {
        if (encryptString == null || encryptString.isEmpty())
            return false;

        int index1 = encryptString.indexOf(tripleEqual);
        if (index1 > -1)
            return true;

        int index2 = encryptString.indexOf(doubleEqual);
        if (index2 > -1) {
            if (index2 == (encryptString.length() - doubleEqual.length())) {
                return false;
            }

            return true;
        }

        int index3 = encryptString.indexOf(equal);
        if (index3 > -1) {
            if (index3 == (encryptString.length() - equal.length())) {
                return false;
            }
        }

        return false;
    }

    private String decryptNetClog(String encryptString) throws Exception {
        if (encryptString == null || encryptString.isEmpty())
            return null;

        try {
            Cipher cipher = getDecryptCipherNet();
            byte[] bytes = Base64.decodeBase64(encryptString.getBytes());
            return new String(cipher.doFinal(bytes), UTF8);
        } catch (Throwable e) {
            return null;
        }
    }

    private String decryptNetCat(String encryptString) throws Exception {
        if (encryptString == null || encryptString.isEmpty())
            return null;

        List<String> parameters = new ArrayList<>();
        String[] array = encryptString.split(splitterAnd);
        if (array != null && array.length > 0) {
            for (String item : array) {
                List<String> parameter = new ArrayList<>();
                if (item.indexOf(tripleEqual) > -1) {
                    processParameter(item, parameter, tripleEqual, doubleEqual);
                } else if (item.indexOf(doubleEqual) > -1) {
                    processParameter(item, parameter, doubleEqual, equal);
                } else {
                    processParameter(item, parameter, equal, "");
                }

                if (parameter.size() > 0) {
                    parameters.add(String.join(equal, parameter));
                }
            }
        }

        String result = "";
        if (parameters.size() > 0) {
            result = String.join(and, parameters);
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        String str = new DecryptResource().decryptNetES("test");
        System.out.println(str);
    }

    private String decryptNetES(String encryptString) throws Exception {
        if (encryptString == null || encryptString.isEmpty())
            return null;

        try {
            Cipher cipher = getDecryptCipherNetPDB();
            byte[] bytes = Base64.decodeBase64(encryptString.getBytes());
            return new String(cipher.doFinal(bytes), UTF8);
        } catch (Throwable e) {
            return null;
        }
    }

    private void processParameter(String item, List<String> parameter, String equal1, String equal2) throws Exception {
        int index = item.indexOf(equal1);
        String temp1 = decryptNetClog(item.substring(0, index).concat(equal2));
        if (temp1 != null && !temp1.isEmpty()) {
            parameter.add(temp1);
        } else {
            parameter.add("");
        }

        String temp2 = decryptNetClog(item.substring(index + equal1.length()));
        if (temp2 != null && !temp2.isEmpty()) {
            parameter.add(temp2);
        } else {
            parameter.add("");
        }

    }

}
