package com.ctrip.sysdev.das.domain;

import java.util.UUID;

import org.msgpack.packer.Packer;

import com.ctrip.sysdev.das.domain.enums.OperationType;

public class Response extends Domain {
	private static final long serialVersionUID = 3910719585739700494L;

	private static Packer packer;
	private UUID taskid;
	private OperationType resultType;
	private int affectRowCount;
	
	private long decodeStart;
	private long decodeEnd;
	private long dbStart;
	private long dbEnd;
	private long encodeStart;
	private long encodeEnd;
	
	public long totalCount;
	
	public Response(Request request) {
		this.decodeStart = request.getDecodeStart();
		this.decodeEnd = request.getDecodeEnd();
		this.taskid = request.getTaskid();
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
	
	public UUID getTaskid() {
		return taskid;
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
