package com.ctrip.sysdev.das.domain;

import java.util.List;
import java.util.UUID;

import org.msgpack.packer.Packer;

import com.ctrip.sysdev.das.domain.enums.OperationType;

public class Response extends Domain {
	private static final long serialVersionUID = 3910719585739700494L;

	private static Packer packer;

	private UUID taskid;

	private OperationType resultType;

	private int affectRowCount;
	
	private int chunkCount;

	private List<List<StatementParameter>> resultSet;
	
	private long totalTime;
	private long decodeRequestTime;
	private long dbTime;
	private long encodeResponseTime;
	
	public static Packer getPacker() {
		return packer;
	}
	
	public static void setPacker(Packer packer) {
		Response.packer = packer;
	}
	
	public int getChunkCount() {
		return chunkCount;
	}
	
	public void setChunkCount(int chunkCount) {
		this.chunkCount = chunkCount;
	}

	public UUID getTaskid() {
		return taskid;
	}

	public void setTaskid(UUID taskid) {
		this.taskid = taskid;
	}

	public OperationType getResultType() {
		return resultType;
	}

	public void setResultType(OperationType resultType) {
		this.resultType = resultType;
	}

	public int getAffectRowCount() {
		return affectRowCount;
	}

	public void setAffectRowCount(int affectRowCount) {
		this.affectRowCount = affectRowCount;
	}

	public List<List<StatementParameter>> getResultSet() {
		return resultSet;
	}

	public void setResultSet(List<List<StatementParameter>> resultSet) {
		this.resultSet = resultSet;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public long getDecodeRequestTime() {
		return decodeRequestTime;
	}

	public void setDecodeRequestTime(long decodeRequestTime) {
		this.decodeRequestTime = decodeRequestTime;
	}

	public long getDbTime() {
		return dbTime;
	}

	public void setDbTime(long dbTime) {
		this.dbTime = dbTime;
	}

	public long getEncodeResponseTime() {
		return encodeResponseTime;
	}

	public void setEncodeResponseTime(long encodeResponseTime) {
		this.encodeResponseTime = encodeResponseTime;
	}
}
