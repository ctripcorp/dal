package com.ctrip.sysdev.das.worker;

import java.util.concurrent.Callable;

import com.ctrip.sysdev.das.commons.DataSourceWrapper;
import com.ctrip.sysdev.das.domain.RequestMessage;
import com.ctrip.sysdev.das.domain.Response;

public class QueryExecutorWrapper implements Callable<Response> {
	private QueryExecutor executor;

	public QueryExecutorWrapper(QueryExecutor executor) {
		this.executor = executor;
	}

	public static QueryExecutorWrapper wrap(QueryExecutor executor) {
		return new QueryExecutorWrapper(executor);
	}
	
	
	public static QueryExecutorWrapper wrap(DataSourceWrapper dataSource, RequestMessage message) {
		return new QueryExecutorWrapper(new QueryExecutor(dataSource, message));
	}
	
	/**
	 * Wrapper method in case we need to use Executors to do multiple threading
	 */
	public Response call() {

		Response resp = executor.execute();

		return resp;
	}
}
