package com.ctrip.datasource.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import com.ctrip.platform.dal.dao.helper.DalBase64;

public class DalEncrypter {

  private final byte[] keyBytes;
  private final Cipher encryptCipher;

  public DalEncrypter(String key)
      throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
    if (key.length() > 8) {
      key = key.substring(0, 8);
    }

    keyBytes = key.getBytes("UTF-8");
    encryptCipher = Cipher.getInstance("DES");
    encryptCipher.init(Cipher.ENCRYPT_MODE, new javax.crypto.spec.SecretKeySpec(keyBytes, "DES"));
  }

  /**
   * For encrypt input/output parameters
   * 
   * @param encryptString
   * @return encrypted value
   */
  public String desEncrypt(String encryptString) {
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
   * 
   * @param encryptString
   * @return dcrypted value
   */
  public String desDecrypt(String encryptString) {
    if (encryptString == null)
      return null;
    try {
      Key key = new javax.crypto.spec.SecretKeySpec(this.keyBytes, "DES");
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
}
