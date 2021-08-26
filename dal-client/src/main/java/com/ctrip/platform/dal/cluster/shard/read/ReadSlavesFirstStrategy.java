package com.ctrip.platform.dal.cluster.shard.read;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.exception.HostNotExpectedException;

import java.util.*;

public class ReadSlavesFirstStrategy implements RouteStrategy {

    protected HashMap<String, List<HostSpec>> hostMap = new HashMap<>();

    @Override
    public void init(Set<HostSpec> hostSpecs) {
        List<HostSpec> masters = new ArrayList<>();
        List<HostSpec> slaves = new ArrayList<>();

        for (HostSpec hostSpec : hostSpecs) {
            if (hostSpec.isMaster())
                masters.add(hostSpec);
            else
                slaves.add(hostSpec);
        }

        hostMap.putIfAbsent(masterRole, masters);
        hostMap.putIfAbsent(slaveRole, slaves);
    }

    @Override
    public HostSpec pickRead(Map<String, Object> map) throws HostNotExpectedException {
        if ((boolean)map.get(slaveOnly) && (boolean)map.get(isPro))
            return slaveOnly();

        List<HostSpec> slaves = hostMap.get(slaveRole);
        if (slaves == null || slaves.isEmpty())
            return pickMaster();

        return choseByRandom(slaves);
    }

    protected HostSpec pickMaster() {
        List<HostSpec> masters = getHostByRole(masterRole);
        return masters.iterator().next();
    }

    protected HostSpec slaveOnly() {
        List<HostSpec> slaves = getHostByRole(slaveRole);
        return choseByRandom(slaves);
    }

    protected List<HostSpec> getHostByRole(String role) {
        List<HostSpec> slaves = hostMap.get(role);
        if (slaves == null || slaves.size() < 1)
            throw new HostNotExpectedException(String.format(NO_HOST_AVAILABLE, role));
        return slaves;
    }

    protected HostSpec loadBalancePickHost(List<HostSpec> hostSpecs) {
        // todo-lhj load-balance strategy implement
        return hostSpecs.iterator().next();
    }



    protected HostSpec choseByRandom(List<HostSpec> hostSpecs) {
        return hostSpecs.get((int)(Math.random() * hostSpecs.size()));
    }

    @Override
    public void onChange(Set<HostSpec> hostSpecs) {

    }

    @Override
    public void dispose() {

    }
}
