package com.ctrip.platform.dal.dao.task;

import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.detectDistributedTransaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.client.DalWatcher;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DalSingleTaskRequest<T> implements DalRequest<int[]>{
	private String logicDbName;
	private DalHints hints;
	private List<Map<String, ?>> daoPojos;
	private SingleTask<T> task;

	private DalSingleTaskRequest(String logicDbName, DalHints hints, SingleTask<T> task) {
		this.logicDbName = logicDbName;
		this.task = task;
		this.hints = hints;
	}
	
	public DalSingleTaskRequest(String logicDbName, DalHints hints, T rawPojo, SingleTask<T> task) {
		this(logicDbName, hints, task);

		if(rawPojo == null) 
			throw new NullPointerException("The given pojo is null.");

		List<T> rawPojos = new ArrayList<>(1);
		rawPojos.add(rawPojo);
		this.daoPojos = task.getPojosFields(rawPojos);
	}

	public DalSingleTaskRequest(String logicDbName, DalHints hints, List<T> rawPojos, SingleTask<T> task) {
		this(logicDbName, hints, task);
		this.daoPojos = task.getPojosFields(rawPojos);
	}
	
	@Override
	public void validate() throws SQLException {
		if(null == daoPojos)
			throw new DalException(ErrorCode.ValidatePojoList);

		if(task == null)
			throw new DalException(ErrorCode.ValidateTask);
		
		detectDistributedTransaction(logicDbName, hints, daoPojos);
	}
	
	@Override
	public boolean isCrossShard() {
		// The single task request is always executed as if the pojos are not corss shard even they really are.
		return false;
	}

	@Override
	public Callable<int[]> createTask() throws SQLException {
		return new SingleTaskCallable<>(hints, daoPojos, task);
	}

	@Override
	public Map<String, Callable<int[]>> createTasks() throws SQLException {
		throw new DalException(ErrorCode.NotSupported);
	}

	@Override
	public ResultMerger<int[]> getMerger() {
		// Not support for now. Maybe support for new hints in the future
		return null;
	}

	private static class SingleTaskCallable<T> implements Callable<int[]> {
		private DalHints hints;
		private List<Map<String, ?>> daoPojos;
		private SingleTask<T> task;

		public SingleTaskCallable(DalHints hints, List<Map<String, ?>> daoPojos, SingleTask<T> task){
			this.hints = hints;
			this.daoPojos = daoPojos;
			this.task = task;
		}

		@Override
		public int[] call() throws Exception {
			int[] counts = new int[daoPojos.size()];
			DalHints localHints = hints.clone();// To avoid shard id being polluted by each pojos
			for (int i = 0; i < daoPojos.size(); i++) {
				DalWatcher.begin();// TODO check if we needed
				try {
					counts[i] = task.execute(localHints, daoPojos.get(i));
				} catch (SQLException e) {
					hints.handleError("Error when execute single pojo operation", e);
				}
			}
			return counts;	
		}
	}
}
