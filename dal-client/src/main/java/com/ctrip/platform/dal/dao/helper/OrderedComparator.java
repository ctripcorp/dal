package com.ctrip.platform.dal.dao.helper;

import java.util.Comparator;

public class OrderedComparator implements Comparator<Ordered> {

    @Override
    public int compare(Ordered o1, Ordered o2) {
        boolean p1 = (o1 instanceof PriorityOrdered);
        boolean p2 = (o2 instanceof PriorityOrdered);
        if (p1 && !p2) {
            return -1;
        }
        else if (p2 && !p1) {
            return 1;
        }
        return (o1.getOrder() < o2.getOrder()) ? -1 : (o1.getOrder() == o2.getOrder() ? 0 : 1);
    }
}
