package com.ctrip.platform.dal.dao.helper;

/**
 * @author wenchao.meng
 *         <p>
 *         Feb 08, 2018
 */
public interface Ordered{

    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    int getOrder();

}
