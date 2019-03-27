package com.ctrip.framework.db.cluster.crypto;

import com.ctrip.framework.db.cluster.config.ConfigManager;
import com.ctrip.framework.db.cluster.util.HttpUtil;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class SecretService {

    private static ConfigManager configManager = ConfigManager.getInstance();
    private String signature;

    @PostConstruct
    void init() throws Exception {
        String sslCode = configManager.getSslCode();
        signature = getSignatureService(sslCode);
    }

    public String getSignature() {
        return signature;
    }

    private String getSignatureService(String sslCode) throws Exception {
        Transaction t = Cat.newTransaction("DB.Cluster.Service.SecretKey.Load", sslCode);
        String signature;
        try {
            String keyServiceUri = configManager.getSecretServiceUrl();
            t.addData("url", keyServiceUri);
            t.addData("sslcode", keyServiceUri);

            String requestBody = "{\"SslCode\":\"" + sslCode + "\",\"InputString\":\"String\",\"EncryptionType\":\"0\"}";
            String responseBody = HttpUtil.getInstance().sendPost(keyServiceUri, null, requestBody);
            signature = parseBody(responseBody);

            t.setStatus(Message.SUCCESS);
            return signature;
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw e;
        } finally {
            t.complete();
        }
    }

    private String parseBody(String responseBody) throws Exception {
        //{"Signature":"1234567890123456","ReturnCode":2,"ResponseStatus":{"Timestamp":"\/Date(1503661407744+0800)\/","Ack":"Success","Errors":[],"Extension":[]},"Message":"error detail info"}
        ObjectMapper mapper = new ObjectMapper();
        SecretKeyResponse secretKeyResponse = mapper.readValue(responseBody, SecretKeyResponse.class);
        SOAResponseStatus responseStatus = secretKeyResponse.getResponseStatus();
        if (responseStatus != null) {
            if (responseStatus.getAck().equals("Success")) {
                if (secretKeyResponse.getReturnCode() == 0) {
                    return secretKeyResponse.getSignature();
                }
                throw new SecretKeyServiceException(secretKeyResponse.getMessage());
            }
            throw new SecretKeyServiceException("SOA2 SecretKey Service Response Ack Fail ");
        }
        throw new SecretKeyServiceException("SOA2 SecretKey Service Response is null ");
    }

}

