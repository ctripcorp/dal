package com.ctrip.framework.dal.dbconfig.plugin.config;

import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.util.MockQconfigService;
import com.google.common.base.Strings;
import org.junit.Before;
import org.junit.Test;
import qunar.tc.qconfig.plugin.QconfigService;


/**
 * @author c7ch23en
 */
public class PluginConfigTest {

    private QconfigService qconfigService;
    private EnvProfile envProfile;

    @Before
    public void init(){
        qconfigService = new MockQconfigService();
        envProfile = new EnvProfile("uat:");
    }

    @Test
    public void testGet() throws Exception {
        PluginConfig config = new PluginConfig(qconfigService, envProfile);
        String keyServiceUri = config.getParamValue(TitanConstants.KEYSERVICE_SOA_URL);
        String sslcode = config.getParamValue(TitanConstants.SSLCODE);
        System.out.println("keyServiceUri=" + keyServiceUri);
        System.out.println("sslcode=" + sslcode);
        assert(!Strings.isNullOrEmpty(keyServiceUri));
        assert(!Strings.isNullOrEmpty(sslcode));
    }

}
