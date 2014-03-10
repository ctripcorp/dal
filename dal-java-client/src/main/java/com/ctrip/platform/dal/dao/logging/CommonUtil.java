package com.ctrip.platform.dal.dao.logging;

import java.security.Key;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class CommonUtil {
    private static final String APPID_COMMENT;
    public static String MACHINE;
    private static String key = "dalctripcn";

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("/* ").append(Logger.getAppId()).append(", ");
        try
        {
        	MACHINE = System.getenv("Host");
        	sb.append(MACHINE);
        }
        catch (Throwable e)
        {
        	MACHINE = "UNKNOW";
            sb.append(MACHINE);
        }
        sb.append("*/\n");
        APPID_COMMENT = sb.toString();
    }
    
    public static String getHashCode4SQLString(String sql)
    {
		byte[] md5Byte = DigestUtils.md5(sql.trim());
		return new String(Base64.encodeBase64(md5Byte));
    }

	public static int getSqlHashCodeForCache(String sql) { 
		String[] sqlSections = sql.split("where");
		// Probably use WHERE instead of where
		if(sqlSections.length == 1) {
			sqlSections = sql.split("WHERE");
		}
		return sqlSections[0].hashCode();
	}

    /**
     * Only add this tag to SQL. No tag for stored procedure.
     * @param sql
     * @return
     */
    public static String tagSql(String sql)
    {
        return APPID_COMMENT + sql;
    }
    


    /**
     * For encrypt input/output parameters
     * @param encryptString
     * @return
     */
    public static String desEncrypt(String encryptString)
    {
        if (encryptString == null) return null;
        
        try {
            byte[] keyBytes = key.substring(0, 8).getBytes("UTF-8");
            Key key = new javax.crypto.spec.SecretKeySpec(keyBytes, "DES");
            Cipher encryptCipher;

        	encryptCipher = Cipher.getInstance("DES");
        	encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            
            
			byte[] inputByteArray = encryptString.getBytes("UTF-8");
			byte[] encryptedByteArray = encryptCipher.doFinal(inputByteArray);
			return new String(Base64.encodeBase64(encryptedByteArray));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
        
//        byte[] keyIV = keyBytes;
//        byte[] inputByteArray = Encoding.UTF8.GetBytes(encryptString);
//        using (DESCryptoServiceProvider provider = new DESCryptoServiceProvider())
//        {
//            MemoryStream mStream = new MemoryStream();
//            CryptoStream cStream = new CryptoStream(mStream, provider.CreateEncryptor(keyBytes, keyIV), CryptoStreamMode.Write);
//            cStream.Write(inputByteArray, 0, inputByteArray.Length);
//            cStream.FlushFinalBlock();
//            return Convert.ToBase64String(mStream.ToArray());
//        }
    }

    /**
     * For decrypt input/output parameters
     * @param encryptString
     * @return
     */
    public static String desDecrypt(String encryptString)
    {
        if (encryptString == null) return null;

		try {
	        byte[] keyBytes = key.substring(0, 8).getBytes("UTF-8");
	        Key key = new javax.crypto.spec.SecretKeySpec(keyBytes, "DES");
	        Cipher decryptCipher;
	        
	        decryptCipher = Cipher.getInstance("DES");
	        decryptCipher.init(Cipher.DECRYPT_MODE, key);
	        
	        byte[] encryptedByteArray = Base64.decodeBase64(encryptString.getBytes());
	        return new String(decryptCipher.doFinal(encryptedByteArray));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return encryptString;
//
//        byte[] keyBytes = Encoding.UTF8.GetBytes(key.Substring(0, 8));
//        byte[] keyIV = keyBytes;
//        byte[] inputByteArray = Convert.FromBase64String(decryptString);
//        using (DESCryptoServiceProvider provider = new DESCryptoServiceProvider())
//        {
//            MemoryStream mStream = new MemoryStream();
//            CryptoStream cStream = new CryptoStream(mStream, provider.CreateDecryptor(keyBytes, keyIV), CryptoStreamMode.Write);
//            cStream.Write(inputByteArray, 0, inputByteArray.Length);
//            cStream.FlushFinalBlock();
//            return Encoding.UTF8.GetString(mStream.ToArray());
//        }
    }
    
    public static void main(String[] args) {
    	String sql = "select * from Person where Id = ?";
    	String hash = getHashCode4SQLString(sql);
    	String enSql = desEncrypt(sql);
    	String deSql = desDecrypt(enSql);
    	System.out.println(sql);
    	System.out.println(hash);
    	System.out.println(enSql);
    	System.out.println(deSql);
    	System.out.println(deSql.equals(sql));
    }
}
