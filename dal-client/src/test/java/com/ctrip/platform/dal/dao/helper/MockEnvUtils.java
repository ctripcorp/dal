package com.ctrip.platform.dal.dao.helper;

/**
 * @author c7ch23en
 */
public class MockEnvUtils extends DefaultEnvUtils {

    private static final String PROD_IDENTITY = "pro";

    private String env;

    @Override
    public String getEnv() {
        return env;
    }

    @Override
    public boolean isProd() {
        String env = getEnv();
        return env != null && env.toLowerCase().contains(PROD_IDENTITY);
    }

    public void setEnv(String env) {
        this.env = env;
    }

}
