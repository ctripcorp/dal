package com.ctrip.platform.dal.dao.configure.dalproperties;

import com.ctrip.platform.dal.common.enums.ImplicitAllShardsSwitch;
import com.ctrip.platform.dal.common.enums.TableParseSwitch;
import com.ctrip.platform.dal.dao.configure.ErrorCodeInfo;

import java.util.Map;

/**
 * Created by lilj on 2018/7/22.
 */
public interface DalPropertiesLocator {

    void setProperties(Map<String, String> properties);

    TableParseSwitch getTableParseSwitch();

    Map<String, ErrorCodeInfo> getErrorCodes();

    ImplicitAllShardsSwitch getImplicitAllShardsSwitch();

    String getClusterInfoQueryUrl();

    String getProperty(String name);

    String getConnectionStringMysqlApiUrl();

    String getStatementInterceptor();

    String getTableParserCacheInitSize(String defaultSize);

    int getTableParserCacheKeyBytes(String defaultBytes);

    String getCustomerClientClassName();

    boolean enableUcsContextLog();

    String ignoreExceptionsForDataSourceMonitor();

}
