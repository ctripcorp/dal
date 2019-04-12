package com.ctrip.datasource.configure;

public class MockNormalLocalConfigureProvider implements LocalConfigureProvider {
    public String getConfigContent(String productName, String configName) throws Exception {
        return "<connectionStrings Version=\"dev\">\n" +
                "\t<add name=\"SimpleShard_0\" connectionString=\"Data Source=DST,1433;UID=sa;password=123456; database=SimpleShard_0;\" providerName=\"System.Data.SqlClient\"/>\n" +
                "</connectionStrings>";
    }
}
