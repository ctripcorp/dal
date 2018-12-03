package com.ctrip.platform.dal.dao.helper;

public class Ordered1 implements Ordered {
    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
