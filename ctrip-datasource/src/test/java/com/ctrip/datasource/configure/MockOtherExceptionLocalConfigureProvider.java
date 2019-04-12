package com.ctrip.datasource.configure;



public class MockOtherExceptionLocalConfigureProvider implements LocalConfigureProvider {
    public String getConfigContent(String productName, String configName) throws Exception {
        throw new Exception("other exception");
    }
}
