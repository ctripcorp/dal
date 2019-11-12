package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.entity.KeyInfo;
import com.ctrip.framework.dal.dbconfig.plugin.entity.Soa2KeyResponse;
import com.ctrip.framework.dal.dbconfig.plugin.entity.Soa2KeyResponseStatus;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.util.RequestExecutor;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;


public class Soa2KeyService implements KeyService {
    private static class Soa2KeyServiceSingletonHolder {
        private static final Soa2KeyService instance = new Soa2KeyService();
    }

    //=== Expose method ===
    public static Soa2KeyService getInstance(){
        return Soa2KeyServiceSingletonHolder.instance;
    }

    //=== Private Constructor ===
    private Soa2KeyService(){
        init();
    }


    private DataSourceCrypto m_dataSourceCrypto;
    private Map<String, KeyInfo> m_keyMap;


    //init variable
    private void init(){
        m_dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
        m_keyMap = new HashMap<String, KeyInfo>();
    }

    @Override
    public KeyInfo getKeyInfo(String sslCode, String keyServiceUri) throws Exception {
        KeyInfo key = m_keyMap.get(sslCode);
        if (key == null) {
            synchronized (m_keyMap) {
                key = m_keyMap.get(sslCode);
                if (key == null) {
//                    //=== Mock code [2017-09-01] ===
//                    //FIXME: Testing code **********************************
//                    if(sslCode.equals("ZI00000000000999")){
//                        key = new KeyInfo().setKey("999").setSslCode(sslCode);
//                    }else {
//                        key = getKeyViaSoaService(sslCode, keyServiceUri);
//                    }
//                    //=== Mock end ===

                    key = getKeyViaSoaService(sslCode, keyServiceUri);
                    m_keyMap.put(sslCode, key);
                }
            }
        }
        return key;
    }

    /**
     * get keyInfo via soa service
     *
     * @param sslCode
     * @param keyServiceUri, eg: https://cscmws.infosec.ctripcorp.com/cscmws2/json/VerifySign
     * @return
     * @throws Exception
     */
    public KeyInfo getKeyViaSoaService(String sslCode, String keyServiceUri) throws Exception {
        Transaction t = Cat.newTransaction("SOAPClient", "VerifySignRequest");

        try {
            // get timeoutMs
            int timeoutMs = RequestExecutor.DEFAULT_TIMEOUT_MS;    //default 10s
            t.addData("url", keyServiceUri);
            t.addData("sslcode", sslCode);
            t.addData("timeoutMs", timeoutMs);
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");
            String request = "{\"SslCode\":\"" + sslCode + "\",\"InputString\":\"String\",\"EncryptionType\":\"PlainText\"}";
            String body = RequestExecutor.getInstance().executePost(keyServiceUri, headers, request, timeoutMs);
            String decodedKey = parseBody(body);
            KeyInfo key = new KeyInfo().setKey(decodedKey).setSslCode(sslCode);

            validateKey(key);

            t.setStatus(Message.SUCCESS);
            return key;
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw e;
        } finally {
            t.complete();
        }
    }

    //parse response body
    public String parseBody(String bodyStr) throws Exception {
        //{"Signature":"1234567890123456","ReturnCode":2,"ResponseStatus":{"Timestamp":"\/Date(1503661407744+0800)\/","Ack":"Success","Errors":[],"Extension":[]},"Message":"error detail info"}
        ObjectMapper mapper = new ObjectMapper();
        Soa2KeyResponse soa2KeyResponse = mapper.readValue(bodyStr, Soa2KeyResponse.class);
        Soa2KeyResponseStatus responseStatus = soa2KeyResponse.getResponseStatus();
        if (responseStatus != null) {
            if (responseStatus.getAck().equals("Success")) {
                if (soa2KeyResponse.getReturnCode() == 0) {
                    return soa2KeyResponse.getSignature();
                }
                throw new DbConfigPluginException(soa2KeyResponse.getMessage());
            }
            throw new DbConfigPluginException("SOA2 Key Service Response Ack Fail ");
        }
        throw new DbConfigPluginException("SOA2 Key Service Response is null ");
    }


    //validate key
    private void validateKey(KeyInfo key) throws Exception {
        String source = "Hello Key!";
        String encryptStr = m_dataSourceCrypto.encrypt(source, key);
        String decryptStr = m_dataSourceCrypto.decrypt(encryptStr, key);

        if (!decryptStr.equals(source)) {
            throw new IllegalStateException(String.format("Invalid key(%s, %s)!", key.getSslCode(), key.getKey()));
        }
    }

    //=== Testing ===
//    public static void main(String[] args) {
//        try {
//            // https://cscmws.infosec.fws.qa.nt.ctripcorp.com/cscmws2/json/VerifySign
//            // http://10.5.90.12:8080/cscmws2/json/verifysign
//            String keyServiceUri = "http://10.5.90.12:8080/cscmws2/json/verifysign";
//            Map<String, String> headers = new HashMap<String, String>();
//            headers.put("Content-Type", "application/json");
//
//            String request = "{\"SslCode\":\"VZ00000000000441\",\"InputString\":\"String\",\"EncryptionType\":\"0\"}";
//            String body = RequestExecutor.ExecutePost(keyServiceUri, headers, request);
//            System.out.println(body);
//            Soa2KeyService service = Soa2KeyService.getInstance();
//            String key = service.parseBody(body);
//            System.out.println(key);
//
//        } catch (Exception ex) {
//            System.out.println(ex.toString());
//        }
//    }

}

