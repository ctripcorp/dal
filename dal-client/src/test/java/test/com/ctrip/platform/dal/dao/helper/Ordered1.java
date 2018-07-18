package test.com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.helper.Ordered;

public class Ordered1 implements Ordered {
    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
