package com.ctrip.platform.dal.dao;

/**
 * @author c7ch23en
 */
public interface PojoExecutionCallback extends DalCallback<PojoExecutionResult> {

    void handle(PojoExecutionResult pojoResult);

}
