package com.ctrip.datasource.configure;

import java.io.FileNotFoundException;

public class MockFileNotFoundLocalConfigureProvider implements LocalConfigureProvider {
    public String getConfigContent(String productName, String configName) throws Exception {
        throw new FileNotFoundException("");
    }
}
