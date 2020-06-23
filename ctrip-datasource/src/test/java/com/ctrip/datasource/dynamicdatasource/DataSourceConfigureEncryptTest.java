package com.ctrip.datasource.dynamicdatasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.DataSourceConfigureConvert;
import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;
import org.junit.Test;

import java.util.Properties;

import static com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants.*;
import static org.junit.Assert.*;

/**
 * Created by taochen on 2019/8/22.
 */
public class DataSourceConfigureEncryptTest {

    @Test
    public void testEncryptDataSourceConfigure() {
        DataSourceConfigureConvert stringConvert = ServiceLoaderHelper.getInstance(DataSourceConfigureConvert.class);
        Properties properties = new Properties();
        properties.setProperty(USER_NAME, "root");
        properties.setProperty(PASSWORD, "!QAZ@WSX1qaz2wsx");
        properties.setProperty(CONNECTION_URL, "jdbc:mysql://DST56614:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;");
        properties.setProperty(DRIVER_CLASS_NAME, "com.mysql.jdbc.Driver");

        DataSourceConfigure configure = new DataSourceConfigure("DalService2DB_w", properties);

        DataSourceConfigure configureEncrypt = stringConvert.desEncrypt(configure);
        assertEquals("root", configure.getUserName());
        assertEquals("!QAZ@WSX1qaz2wsx", configure.getPassword());
        assertNotEquals("root", configureEncrypt.getUserName());
        assertNotEquals("!QAZ@WSX1qaz2wsx", configureEncrypt.getPassword());

        DataSourceConfigure configureDecrypt = stringConvert.desDecrypt(configureEncrypt);
        assertNotEquals("root", configureEncrypt.getUserName());
        assertNotEquals("!QAZ@WSX1qaz2wsx", configureEncrypt.getPassword());
        assertEquals("root", configureDecrypt.getUserName());
        assertEquals("!QAZ@WSX1qaz2wsx", configureDecrypt.getPassword());
    }

}
