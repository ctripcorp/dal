package com.ctrip.platform.dal.dao.helper;

/**
 * @author c7ch23en
 */
public class NullEnvUtils implements EnvUtils {

    @Override
    public String getEnv() {
        return null;
    }

    @Override
    public String getSubEnv() {
        return null;
    }

    @Override
    public String getZone() {
        return null;
    }

    @Override
    public String getIdc() {
        return null;
    }

}
