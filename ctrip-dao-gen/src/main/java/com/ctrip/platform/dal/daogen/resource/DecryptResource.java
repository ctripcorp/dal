package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.decrypt.DecryptInfo;
import com.ctrip.platform.dal.sql.logging.CommonUtil;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Resource
@Singleton
@Path("decryption")
public class DecryptResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("decrypt")
    public DecryptInfo getDecryptInfo(@QueryParam("encrypt") String encrypt) {
        DecryptInfo result = new DecryptInfo();
        try {
            String decrypt = CommonUtil.desDecrypt(encrypt);
            result.setDecryptMsg(decrypt);
            result.setErrorMsg("");
        } catch (Throwable e) {
            result.setErrorMsg(e.getMessage());
        }

        return result;
    }
}
