package com.ctrip.platform.dal.cluster.base;

import java.util.Comparator;

public class ComponentOrderedComparator implements Comparator<ComponentOrdered> {

    @Override
    public int compare(ComponentOrdered o1, ComponentOrdered o2) {
        return (o1.getOrder() < o2.getOrder()) ? -1 : (o1.getOrder() == o2.getOrder() ? 0 : 1);
    }

}
