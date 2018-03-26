package com.ctrip.platform.dal.sql.logging;

import java.security.Key;

import javax.crypto.Cipher;

import com.ctrip.datasource.util.DalEncrypter;
import com.ctrip.platform.dal.dao.client.LoggerAdapter;
import com.ctrip.platform.dal.dao.helper.DalBase64;


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
	
	private static DalEncrypter encryptCipher;
	
	static {
		try {
		    encryptCipher = new DalEncrypter(LoggerAdapter.secretKey);
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
	    return encryptCipher.desEncrypt(encryptString);
    }
	
	/**
     * For decrypt input/output parameters
     * @param encryptString
     * @return dcrypted value
     */
	public static String desDecrypt(String encryptString) {
	    return encryptCipher.desDecrypt(encryptString);
	}
}
