package com.ctrip.framework.idgen.client.strategy;

public class ConstantStrategy extends DefaultStrategy {

    private boolean flag;

    public ConstantStrategy(boolean flag) {
        this.flag = flag;
    }

    @Override
    public boolean checkIfNeedPrefetch() {
        return flag;
    }

}
