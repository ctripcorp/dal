package com.ctrip.platform.dal.dao.base;

import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.client.ConnectionAction;

public class MockConnectionAction extends ConnectionAction {

    public MockConnectionAction(DalEventEnum operation) {
        super.operation = operation;
    }

    @Override
    public Object execute() throws Exception {
        return null;
    }
}
