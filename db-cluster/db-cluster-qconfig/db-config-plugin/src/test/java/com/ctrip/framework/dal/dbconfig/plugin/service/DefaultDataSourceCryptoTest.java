package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.entity.KeyInfo;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import org.apache.commons.codec.binary.StringUtils;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class DefaultDataSourceCryptoTest {

    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();

    @Test
    public void testEncrypt() throws Exception {
        String source = "hello";
        KeyInfo keyInfo = buildKeyInfo();
        String cipherText = dataSourceCrypto.encrypt(source, keyInfo);
        assert (!StringUtils.equals(source, cipherText));
        System.out.println("source=[" + source + "], cipherText=[" + cipherText + "]");
    }

    @Test
    public void testDecrypt() throws Exception {
        String cipherText = "1C41A19BDBE3719EA3F80FED207458EF";
        KeyInfo keyInfo = buildKeyInfo();
        String originalText = dataSourceCrypto.decrypt(cipherText, keyInfo);
        assert (!StringUtils.equals(cipherText, originalText));
        System.out.println("cipherText=[" + cipherText + "], originalText=[" + originalText + "]");
    }

    @Test
    public void testEnDecrypt() throws Exception {
        String source = "study";
        KeyInfo keyInfo = buildKeyInfo();
        String cipherText = dataSourceCrypto.encrypt(source, keyInfo);
        System.out.println("source=[" + source + "], cipherText=[" + cipherText + "]");
        assert (!StringUtils.equals(source, cipherText));
        String originalText = dataSourceCrypto.decrypt(cipherText, keyInfo);
        System.out.println("cipherText=[" + cipherText + "], originalText=[" + originalText + "]");
        assert (StringUtils.equals(originalText, source));
    }

//    @Test
    public void testDecryptCost() throws Exception {
        final KeyInfo keyInfo = buildKeyInfo();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        decrypt(keyInfo);
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        Thread.currentThread().join();
    }

    private void decrypt(KeyInfo keyInfo) {
        Transaction transaction = Cat.newTransaction("Decrypt.Cost", "decrypt");
        try {
            String cipherText = "1C41A19BDBE3719EA3F80FED207458EF";
            String originalText = dataSourceCrypto.decrypt(cipherText, keyInfo);
            transaction.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
            Cat.logError(e);
        } finally {
            transaction.complete();
        }
    }

    //build init keyInfo
    private KeyInfo buildKeyInfo() {
        String m_key = "1234567890123456";
        String m_sslCode = "VZ00000000000441_test";
        KeyInfo key = new KeyInfo();
        key.setKey(m_key).setSslCode(m_sslCode);
        return key;
    }

}
