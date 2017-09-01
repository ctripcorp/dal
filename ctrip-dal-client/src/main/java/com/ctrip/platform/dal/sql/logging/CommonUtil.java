package com.ctrip.platform.dal.sql.logging;

import java.security.Key;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import com.ctrip.platform.dal.dao.client.LoggerAdapter;


public class CommonUtil {
	
	private static String key = LoggerAdapter.secretKey;

	public static String null2NA(String str) {
		return null != str ? str : "NA";
	}
	
	public static String string2Json(String s) {
		if(null == s) return "";
        StringBuffer sb = new StringBuffer();        
        for (int i=0; i<s.length(); i++) {  
            char c = s.charAt(i);    
             switch (c){  
             case '\"':        
                 sb.append("\\\"");        
                 break;        
             case '\\':        
                 sb.append("\\\\");        
                 break;        
             case '/':        
                 sb.append("\\/");        
                 break;        
             case '\b':        
                 sb.append("\\b");        
                 break;        
             case '\f':        
                 sb.append("\\f");        
                 break;        
             case '\n':        
                 sb.append("\\n");        
                 break;        
             case '\r':        
                 sb.append("\\r");        
                 break;        
             case '\t':        
                 sb.append("\\t");        
                 break;        
             default:        
                 sb.append(c);     
             }  
         }      
        return sb.toString();     
	}
	
	private static Cipher encryptCipher;
	
	static {
		try {
			byte[] keyBytes = key.substring(0, 8).getBytes("UTF-8");
			Key key = new javax.crypto.spec.SecretKeySpec(keyBytes, "DES");
			encryptCipher = Cipher.getInstance("DES");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
     * For encrypt input/output parameters
     * @param encryptString
     * @return encrypted value
     */
	public static String desEncrypt(String encryptString) {
		if (encryptString == null)
			return null;
		try {
			byte[] inputByteArray = encryptString.getBytes("UTF-8");
			byte[] encryptedByteArray = encryptCipher.doFinal(inputByteArray);
			return new String(DalBase64.encodeBase64(encryptedByteArray));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
        
    }
	
	/**
     * For decrypt input/output parameters
     * @param encryptString
     * @return dcrypted value
     */
	public static String desDecrypt(String encryptString) {
		if (encryptString == null)
			return null;
		try {
			byte[] keyBytes = key.substring(0, 8).getBytes("UTF-8");
			Key key = new javax.crypto.spec.SecretKeySpec(keyBytes, "DES");
			Cipher decryptCipher;

			decryptCipher = Cipher.getInstance("DES");
			decryptCipher.init(Cipher.DECRYPT_MODE, key);

			byte[] encryptedByteArray = DalBase64.decodeBase64(encryptString.getBytes());
			return new String(decryptCipher.doFinal(encryptedByteArray));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptString;
    }
	
    private static class DalBase64 extends Base64 {
        protected int  getDefaultBufferSize() {
            return 256;
        }
    }
}
