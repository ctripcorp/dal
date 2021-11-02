package com.ctrip.platform.dal.dao.helper;

/**
 * @author c7ch23en
 */
public class DefaultEnvUtils implements EnvUtils {

    @Override
    public String getZone() {
        return "sharb";
    }

    @Override
    public boolean isProd() {
        return false;
    }
}
