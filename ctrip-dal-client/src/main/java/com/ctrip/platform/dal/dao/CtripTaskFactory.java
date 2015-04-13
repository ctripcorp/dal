package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.task.TaskFactory;

/**
 * This DAO is to simplify Ctrip special MS Sql Server CUD case. 
 * Ctrip use SP3 or SPA to perform CUD on MS Sql Server.
 * The rules:
 * 1. If there are both SP3 and SPA for the table, the batch CUD will use SP3, the non-batch will use SPA.
 *    The reason is because a special setting in Ctrip Sql Server that prevent batch SPA CUD
 * 2. If there is only SP3 for the table, both batch and non-batch will using SP3
 * 3. If there is only SPA for the table, only non-batch CUD supported
 * 4. If there is no SP3 or SPA, the original DalTableDao should be used.
 * 
 * For sharding support: it is confirmed from DBA that Ctrip has shard by DB case, but no shard by table case.
 * For inout, out parameter: only insert SP3/SPA has inout/out parameter
 * 
 * @author jhhe
 */
public class CtripTaskFactory<T> implements TaskFactory<T>{

}
