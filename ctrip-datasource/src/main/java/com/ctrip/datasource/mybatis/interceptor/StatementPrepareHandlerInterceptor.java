package com.ctrip.datasource.mybatis.interceptor;

import java.sql.Connection;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Signature;

/**
 * For statement handler prior to mybatis 3.4.0
 */
@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})
})
public class StatementPrepareHandlerInterceptor extends AbstractStatementPrepareHandlerInterceptor{
}
