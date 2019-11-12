package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.google.common.base.Strings;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class Soa2KeyServiceTest {

    private KeyService service = Soa2KeyService.getInstance();

    @Test
    public void testGetKey() throws Exception {
        String sslCode = "VZ00000000000441";
        String keyServiceUri = "https://cscmws.infosec.fws.qa.nt.ctripcorp.com/cscmws2/json/VerifySign";
        String key = service.getKeyInfo(sslCode, keyServiceUri).getKey();
        System.out.println("key=" + key);
        assert(!Strings.isNullOrEmpty(key));
    }

}
