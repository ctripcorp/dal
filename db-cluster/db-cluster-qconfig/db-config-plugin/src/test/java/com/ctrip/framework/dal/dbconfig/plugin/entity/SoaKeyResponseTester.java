package com.ctrip.framework.dal.dbconfig.plugin.entity;

import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.google.common.base.Strings;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by lzyan on 2017/8/25.
 */
public class SoaKeyResponseTester {

    @Test
    public void testObj2Json(){
        Soa2KeyResponse soa2KeyResponse = buildSoa2KeyResponse();
        String soa2KeyResp = GsonUtils.t2Json(soa2KeyResponse);
        System.out.println(soa2KeyResp);
        assert(!Strings.isNullOrEmpty(soa2KeyResp));
    }

    @Test
    public void testJson2Obj(){
        String mhaInput = "{\"Signature\":\"1234567890123456\",\"ReturnCode\":0,\"ResponseStatus\":{\"Timestamp\":\"\\/Date(1503661407744+0800)\\/\",\"Ack\":\"Success\",\"Errors\":[],\"Extension\":[]},\"Message\":\"error detail info\"}";
        Soa2KeyResponse soa2KeyResponse = GsonUtils.json2T(mhaInput,  Soa2KeyResponse.class);
        System.out.println("soa2KeyResponse=\n" + soa2KeyResponse);
        assert(soa2KeyResponse != null);
    }

    //build test obj
    private Soa2KeyResponse buildSoa2KeyResponse(){
        //{"Signature":"1234567890123456","ReturnCode":0,"ResponseStatus":{"Timestamp":"\/Date(1503661407744+0800)\/","Ack":"Success","Errors":[],"Extension":[]},"Message":"error detail info"}

        Soa2KeyResponseStatus responseStatus = new Soa2KeyResponseStatus();
        responseStatus.setTimestamp("/Date(1503661407744+0800)/");
        responseStatus.setAck("Success");
        responseStatus.setErrors(new ArrayList<String>());
        responseStatus.setExtension(new ArrayList<String>());
        Soa2KeyResponse soa2KeyResponse = new Soa2KeyResponse();
        soa2KeyResponse.setSignature("1234567890123456");
        soa2KeyResponse.setReturnCode(0);
        soa2KeyResponse.setResponseStatus(responseStatus);
        soa2KeyResponse.setMessage(null);

        return soa2KeyResponse;

    }


}
