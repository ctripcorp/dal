package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.PojoExecutionResult;

/**
 * @author c7ch23en
 */
public class PojoExecutionResultImpl extends ExecutionResultImpl<Integer> implements PojoExecutionResult {

    private int pojoIndex;

    public PojoExecutionResultImpl(int pojoIndex, Integer result) {
        super(result);
        init(pojoIndex);
    }

    public PojoExecutionResultImpl(int pojoIndex, Throwable errorCause) {
        super(errorCause);
        init(pojoIndex);
    }

    private void init(int pojoIndex) {
        this.pojoIndex = pojoIndex;
    }

    @Override
    public int getPojoIndex() {
        return pojoIndex;
    }

}
