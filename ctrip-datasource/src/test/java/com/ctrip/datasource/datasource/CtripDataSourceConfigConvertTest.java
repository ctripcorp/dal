package com.ctrip.datasource.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class CtripDataSourceConfigConvertTest {

    private CtripDataSourceConfigConvert converter = new CtripDataSourceConfigConvert();

    @Test
    public void testEncryptEmptyConfig() {
        DataSourceConfigure config = converter.desEncrypt(new DataSourceConfigure());
        Assert.assertNull(config.getUserName());
        Assert.assertNull(config.getPassword());
    }

    @Test
    public void testDecryptEmptyConfig() {
        DataSourceConfigure config = converter.desDecrypt(new DataSourceConfigure());
        Assert.assertNull(config.getUserName());
        Assert.assertNull(config.getPassword());
    }

}
