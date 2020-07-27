package com.ctrip.datasource.util;

import com.ctrip.framework.dal.cluster.client.util.ObjectHolder;
import com.ctrip.framework.dal.cluster.client.util.ValueWrapper;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.helper.DefaultEnvUtils;
import com.ctrip.platform.dal.dao.helper.EnvUtils;

/**
 * @author c7ch23en
 */
public class CtripEnvUtils extends DefaultEnvUtils implements EnvUtils {

    private static final String PROD_IDENTITY = "pro";

    private final ObjectHolder<ValueWrapper<String>> env = new ObjectHolder<>();
    private final ObjectHolder<ValueWrapper<String>> subEnv = new ObjectHolder<>();
    private final ObjectHolder<ValueWrapper<String>> zone = new ObjectHolder<>();
    private final ObjectHolder<ValueWrapper<String>> idc = new ObjectHolder<>();

    @Override
    public String getEnv() {
        return env.getOrCreate(() -> new ValueWrapper<>(Foundation.server().getEnv().getName())).getValue();
    }

    @Override
    public String getSubEnv() {
        return subEnv.getOrCreate(() -> new ValueWrapper<>(Foundation.server().getSubEnv())).getValue();
    }

    @Override
    public String getZone() {
        return zone.getOrCreate(() -> new ValueWrapper<>(Foundation.server().getZone())).getValue();
    }

    @Override
    public String getIdc() {
        return idc.getOrCreate(() -> new ValueWrapper<>(Foundation.server().getDataCenter())).getValue();
    }

    public void setEnv(String env) {
        if (env != null)
            this.env.set(new ValueWrapper<>(env));
        else
            this.env.set(null);
    }

    public void setSubEnv(String subEnv) {
        if (subEnv != null)
            this.subEnv.set(new ValueWrapper<>(subEnv));
        else
            this.subEnv.set(null);
    }

    public void setZone(String zone) {
        if (zone != null)
            this.zone.set(new ValueWrapper<>(zone));
        else
            this.zone.set(null);
    }

    public void setIdc(String idc) {
        if (idc != null)
            this.idc.set(new ValueWrapper<>(idc));
        else
            this.idc.set(null);
    }

    @Override
    public boolean isProd() {
        String env = getEnv();
        return env != null && env.toLowerCase().contains(PROD_IDENTITY);
    }

    public void clear() {
        setEnv(null);
        setSubEnv(null);
        setZone(null);
        setIdc(null);
    }

}
