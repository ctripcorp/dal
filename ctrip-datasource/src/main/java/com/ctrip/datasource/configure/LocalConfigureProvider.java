package com.ctrip.datasource.configure;

public interface LocalConfigureProvider {
   String getConfigContent(String productName, String configName) throws Exception;
}
