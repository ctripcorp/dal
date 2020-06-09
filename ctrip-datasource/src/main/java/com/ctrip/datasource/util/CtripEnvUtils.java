package com.ctrip.datasource.util;

import com.ctrip.framework.dal.cluster.client.util.ObjectHolder;
import com.ctrip.framework.dal.cluster.client.util.ValueWrapper;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.helper.EnvUtils;

/**
 * @author c7ch23en
 */
public class CtripEnvUtils implements EnvUtils {

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
        this.env.set(new ValueWrapper<>(env));
    }

    public void setSubEnv(String subEnv) {
        this.subEnv.set(new ValueWrapper<>(subEnv));
    }

    public void setZone(String zone) {
        this.zone.set(new ValueWrapper<>(zone));
    }

    public void setIdc(String idc) {
        this.idc.set(new ValueWrapper<>(idc));
    }

}
