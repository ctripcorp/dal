package com.ctrip.datasource.mybatis.interceptor;

import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import com.ctrip.datasource.mybatis.DalMybatisContextHolder;

/**
 * For statement handler in mybatis 3.4.0+
 */
@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class NewStatementPrepareHandlerInterceptor extends AbstractStatementPrepareHandlerInterceptor {
}
