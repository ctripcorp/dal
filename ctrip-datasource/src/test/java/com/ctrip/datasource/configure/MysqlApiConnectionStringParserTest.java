package com.ctrip.datasource.configure;

import com.ctrip.datasource.util.MysqlApiConnectionStringUtils;
import com.ctrip.datasource.util.entity.ClusterNodeInfo;
import com.ctrip.datasource.util.entity.MysqlApiConnectionStringInfo;
import com.ctrip.datasource.util.entity.MysqlApiConnectionStringInfoResponse;
import com.ctrip.platform.dal.common.enums.DBModel;
import com.ctrip.platform.dal.dao.configure.DalConnectionStringConfigure;
import com.ctrip.platform.dal.dao.helper.JsonUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class MysqlApiConnectionStringParserTest {
    private static final String DBNAME = "qconfig";
    //private static final String NOT_EXIST_DBNAME = "dalTest0000";
    private static final String DBNAME_1 = "fxdalclusterbenchmarkdb";
    private static final String TOKEN = "2olkdweut7sUbsrim-La";
    private static final String TOKEN_1 = "a{GhvyvnzyqCuxykg02a";

    @Test
    public void testConnectionStringParser() throws Exception {
        String mgrUrl = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.9.72.67)(port=55944),address=(type=master)(protocol=tcp)(host=10.25.82.137)(port=55944),address=(type=master)(protocol=tcp)(host=10.60.53.211)(port=55944)/qconfig?useUnicode=true&characterEncoding=UTF-8";
        String url = "jdbc:mysql://qconfig.mysql.db.fat.qa.nt.ctripcorp.com:55111/qconfig?useUnicode=true&characterEncoding=UTF-8";
        MysqlApiConnectionStringInfo info = MysqlApiConnectionStringUtils.getConnectionStringFromMysqlApi(null, DBNAME, "PRO");
        DalConnectionStringConfigure configure = MysqlApiConnectionStringParser.getInstance().parser(DBNAME, info, TOKEN, DBModel.MGR);
        Assert.assertEquals(mgrUrl, configure.getConnectionUrl());
        MysqlApiConnectionStringInfo info1 = MysqlApiConnectionStringUtils.getConnectionStringFromMysqlApi(null, DBNAME, "FAT");
        DalConnectionStringConfigure configure1 = MysqlApiConnectionStringParser.getInstance().parser(DBNAME, info1, TOKEN, DBModel.STANDALONE);
        Assert.assertEquals(url, configure1.getConnectionUrl());
    }

    @Test
    public void testConnectionStringParserFixedInfo() throws UnsupportedEncodingException {
        String responseStr = "{\n" +
                "    \"message\": \"ok\",\n" +
                "    \"data\": {\n" +
                "        \"connectionstring\": \"Server=fxdalclusterbenchmark.mysql.db.ctripcorp.com;port=55944;UID=m_fxdamark_oc;password=YTIwZ2t5eHVDcXl6bnZ5dmhHe2F4d2tiI2F5YXAwUWtwaDZ0bXdxVg==;database=fxdalclusterbenchmarkdb;\",\n" +
                "        \"clusternodeinfolist\": [\n" +
                "            {\n" +
                "                \"status\": \"online\",\n" +
                "                \"mastervip\": \"10.25.91.205\",\n" +
                "                \"machine_name\": \"VMS106602\",\n" +
                "                \"machine_located_short\": \"SHAJQ\",\n" +
                "                \"service_ip\": \"10.8.37.82\",\n" +
                "                \"cluster_name\": \"fxqconfigtest\",\n" +
                "                \"dns_port\": 55944,\n" +
                "                \"datadir\": \"/data/mysql/\",\n" +
                "                \"machine_located\": \"上海金桥IDC(联通)\",\n" +
                "                \"role\": \"slave-dr\",\n" +
                "                \"ip_business_gateway\": \"10.8.36.254\",\n" +
                "                \"ip_business\": \"10.8.37.82\",\n" +
                "                \"errmsg\": \"\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"status\": \"online\",\n" +
                "                \"mastervip\": \"10.25.91.205\",\n" +
                "                \"machine_name\": \"VMS108182\",\n" +
                "                \"machine_located_short\": \"SHAOY\",\n" +
                "                \"service_ip\": \"10.25.91.204\",\n" +
                "                \"cluster_name\": \"fxqconfigtest\",\n" +
                "                \"dns_port\": 55944,\n" +
                "                \"datadir\": \"/data/mysql/\",\n" +
                "                \"machine_located\": \"上海欧阳IDC(电信)\",\n" +
                "                \"role\": \"slave-dr\",\n" +
                "                \"ip_business_gateway\": \"10.25.88.254\",\n" +
                "                \"ip_business\": \"10.25.91.204\",\n" +
                "                \"errmsg\": \"\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"status\": \"online\",\n" +
                "                \"mastervip\": \"10.25.91.205\",\n" +
                "                \"machine_name\": \"VMS131568\",\n" +
                "                \"machine_located_short\": \"SHARB\",\n" +
                "                \"service_ip\": \"10.60.45.198\",\n" +
                "                \"cluster_name\": \"fxqconfigtest\",\n" +
                "                \"dns_port\": 55944,\n" +
                "                \"datadir\": \"/data/mysql/\",\n" +
                "                \"machine_located\": \"上海日阪IDC(联通)\",\n" +
                "                \"role\": \"master\",\n" +
                "                \"ip_business_gateway\": \"10.60.44.1\",\n" +
                "                \"ip_business\": \"10.60.45.198\",\n" +
                "                \"errmsg\": \"\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"clustertype\": \"mgr\"\n" +
                "    },\n" +
                "    \"success\": true\n" +
                "}";

        String mgr_mgr_url_three = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.8.37.82)(port=55944),address=(type=master)(protocol=tcp)(host=10.25.91.204)(port=55944),address=(type=master)(protocol=tcp)(host=10.60.45.198)(port=55944)/fxdalclusterbenchmarkdb?useUnicode=true&characterEncoding=UTF-8";
        String mgr_mgr_url_two ="jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.25.91.204)(port=55944),address=(type=master)(protocol=tcp)(host=10.60.45.198)(port=55944)/fxdalclusterbenchmarkdb?useUnicode=true&characterEncoding=UTF-8";
        String master_jdbc_url = "jdbc:mysql://10.60.45.198:55944/fxdalclusterbenchmarkdb?useUnicode=true&characterEncoding=UTF-8";
        String slave_jdbc_url_1 = "jdbc:mysql://10.25.91.204:55944/fxdalclusterbenchmarkdb?useUnicode=true&characterEncoding=UTF-8";

        String url = "jdbc:mysql://fxdalclusterbenchmark.mysql.db.ctripcorp.com:55944/fxdalclusterbenchmarkdb?useUnicode=true&characterEncoding=UTF-8";
        MysqlApiConnectionStringInfoResponse response = JsonUtils.fromJson(responseStr, MysqlApiConnectionStringInfoResponse.class);
        MysqlApiConnectionStringInfo info = response.getData();

        DalConnectionStringConfigure configure1 = MysqlApiConnectionStringParser.getInstance().parser(DBNAME_1, info, TOKEN_1, DBModel.MGR);
        Assert.assertEquals(mgr_mgr_url_three, configure1.getConnectionUrl());

        List<ClusterNodeInfo> clusterNodeInfos = info.getClusternodeinfolist();
        for (ClusterNodeInfo clusterNodeInfo : clusterNodeInfos) {
            if (clusterNodeInfo.getRole().startsWith("slave")) {
                clusterNodeInfo.setStatus("offline");
                break;
            }
        }
        DalConnectionStringConfigure configure4 = MysqlApiConnectionStringParser.getInstance().parser(DBNAME_1, info, TOKEN_1, DBModel.MGR);
        Assert.assertEquals(mgr_mgr_url_two, configure4.getConnectionUrl());

        info.setClustertype("mha");
        DalConnectionStringConfigure configure2 = MysqlApiConnectionStringParser.getInstance().parser(DBNAME_1, info, TOKEN_1, DBModel.MGR);
        Assert.assertEquals(master_jdbc_url, configure2.getConnectionUrl());


        for (ClusterNodeInfo clusterNodeInfo : clusterNodeInfos) {
            if (clusterNodeInfo.getRole().equalsIgnoreCase("master"))
            clusterNodeInfo.setStatus("offline");
        }
        info.setClustertype("mgr");
        DalConnectionStringConfigure configure6 = MysqlApiConnectionStringParser.getInstance().parser(DBNAME_1, info, TOKEN_1, DBModel.MGR);
        Assert.assertEquals(slave_jdbc_url_1, configure6.getConnectionUrl());

        info.setClustertype("mha");
        DalConnectionStringConfigure configure3 = MysqlApiConnectionStringParser.getInstance().parser(DBNAME_1, info, TOKEN_1, DBModel.MGR);
        Assert.assertNull(configure3);

        info.setClustertype("mgr");
        for (ClusterNodeInfo clusterNodeInfo : clusterNodeInfos) {
            if ("online".equalsIgnoreCase(clusterNodeInfo.getStatus())) {
                clusterNodeInfo.setStatus("offline");
                break;
            }
        }
        DalConnectionStringConfigure configure7 = MysqlApiConnectionStringParser.getInstance().parser(DBNAME_1, info, TOKEN_1, DBModel.MGR);
        Assert.assertNull(configure7);

        DalConnectionStringConfigure configure5 = MysqlApiConnectionStringParser.getInstance().parser(DBNAME_1, info, TOKEN_1, DBModel.STANDALONE);
        Assert.assertEquals(url, configure5.getConnectionUrl());
    }
}
