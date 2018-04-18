package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.decrypt.DecryptInfo;
import org.apache.commons.codec.binary.Base64;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.net.URLDecoder;
import java.security.Key;

@Resource
@Singleton
@Path("decryption")
public class DecryptResource {
    public static String secretKey = "dalctripcn";

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

    private static String desDecrypt(String encryptString) throws Exception {
        if (encryptString == null)
            return null;
        try {
            byte[] keyBytes = secretKey.substring(0, 8).getBytes("UTF-8");
            Key key = new javax.crypto.spec.SecretKeySpec(keyBytes, "DES");
            Cipher decryptCipher;

            decryptCipher = Cipher.getInstance("DES");
            decryptCipher.init(Cipher.DECRYPT_MODE, key);

            byte[] encryptedByteArray = Base64.decodeBase64(encryptString.getBytes());
            return new String(decryptCipher.doFinal(encryptedByteArray));
        } catch (Throwable e) {
            throw e;
        }
    }

}
