package com.ctrip.sysdev.das.domain;

import io.netty.util.AttributeKey;

import java.util.List;
import java.util.UUID;

import org.msgpack.packer.Packer;

import com.ctrip.sysdev.das.domain.enums.OperationType;

public class Response extends Domain {
	private static final long serialVersionUID = 3910719585739700494L;
	public static final AttributeKey<Response> RESPONSE_KEY =
	            new AttributeKey<Response>("RESPONSE_KEY");

	private static Packer packer;

	private UUID taskid;

	private OperationType resultType;

	private int affectRowCount;
	
	private int chunkCount;

	private List<List<StatementParameter>> resultSet;
	
	private long decodeStart;
	private long decodeEnd;
	private long dbStart;
	private long dbEnd;
	private long encodeStart;
	private long encodeEnd;
	
	public long totalCount;
	
	public Response() {
		decodeStart();
	}
	
	public void decodeStart() {
		decodeStart = System.currentTimeMillis();
	}
	public void decodeEnd() {
		decodeEnd = System.currentTimeMillis();
	}
	public void dbStart() {
		dbStart = System.currentTimeMillis();
	}
	public void dbEnd() {
		dbEnd = System.currentTimeMillis();
	}
	public void encodeStart() {
		encodeStart = System.currentTimeMillis();
	}
	public void encodeEnd() {
		encodeEnd = System.currentTimeMillis();
	}
	
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
		return encodeEnd - decodeStart;
	}

	public long getDecodeRequestTime() {
		return decodeEnd - decodeStart;
	}

	public long getDbTime() {
		return dbEnd - dbStart;
	}

	public long getEncodeResponseTime() {
		return encodeEnd - encodeStart;
	}
}
