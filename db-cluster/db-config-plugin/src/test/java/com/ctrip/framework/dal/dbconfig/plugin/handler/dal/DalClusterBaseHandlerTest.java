package com.ctrip.framework.dal.dbconfig.plugin.handler.dal;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DalClusterEntity;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.configure.DalConfigure;
import com.ctrip.framework.dal.dbconfig.plugin.util.DalClusterUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.XmlUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.plugin.PluginResult;
import qunar.tc.qconfig.plugin.QconfigService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class DalClusterBaseHandlerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DalClusterBaseHandlerTest.class);

    @Test
    public void testBuildConfigContent() throws Exception {
        DalClusterEntity entity = new DalClusterEntity();
        entity.setClusterName("mockCluster");
        entity.setDbCategory("mysql");
        entity.setVersion(1);
        entity.setDatabaseShards(new ArrayList<>());
        entity.setShardStrategies("<Cluster name=\"mockCluster\" dbCategory=\"mysql\" version=\"1\">\n" +
                "        <DatabaseShards/>\n" +
                "        <SslCode>mockSslCode</SslCode>\n" +
                "        <Operator>mockOperator</Operator>\n" +
                "        <UpdateTime>2019-11-11 15:37:43</UpdateTime>\n" +
                "    </Cluster>");
        entity.setIdGenerators(null);
        entity.setSslCode("mockSslCode");
        entity.setOperator("mockOperator");
        DalConfigure configure = DalClusterUtils.formatCluster2Configure(entity);
        String xml = XmlUtils.toXml(configure);
        LOGGER.info(String.format("Formatted xml:\n%s", xml));
    }

}
