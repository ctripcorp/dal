package com.ctrip.sysdev.das.domain;

import java.util.List;
import java.util.UUID;

import org.msgpack.packer.Packer;

import com.ctrip.sysdev.das.domain.enums.OperationType;

public class Response extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3910719585739700494L;

	private static Packer packer;

	private UUID taskid;

	private OperationType resultType;

	private int affectRowCount;
	
	private int chunkCount;

	private List<List<StatementParameter>> resultSet;
	

	/**
	 * @return the packer
	 */
	public static Packer getPacker() {
		return packer;
	}
	
	/**
	 * @param packer
	 *            the packer to set
	 */
	public static void setPacker(Packer packer) {
		Response.packer = packer;
	}
	
	/**
	 * 
	 * @return the chunk count
	 */
	public int getChunkCount() {
		return chunkCount;
	}
	
	/**
	 * 
	 * @param chunkCount
	 */
	public void setChunkCount(int chunkCount) {
		this.chunkCount = chunkCount;
	}

	/**
	 * @return the taskid
	 */
	public UUID getTaskid() {
		return taskid;
	}

	/**
	 * @param taskid
	 *            the taskid to set
	 */
	public void setTaskid(UUID taskid) {
		this.taskid = taskid;
	}

	/**
	 * @return the resultType
	 */
	public OperationType getResultType() {
		return resultType;
	}

	/**
	 * @param resultType
	 *            the resultType to set
	 */
	public void setResultType(OperationType resultType) {
		this.resultType = resultType;
	}

	/**
	 * @return the affectRowCount
	 */
	public int getAffectRowCount() {
		return affectRowCount;
	}

	/**
	 * @param affectRowCount
	 *            the affectRowCount to set
	 */
	public void setAffectRowCount(int affectRowCount) {
		this.affectRowCount = affectRowCount;
	}

	/**
	 * @return the resultSet
	 */
	public List<List<StatementParameter>> getResultSet() {
		return resultSet;
	}

	/**
	 * @param resultSet
	 *            the resultSet to set
	 */
	public void setResultSet(List<List<StatementParameter>> resultSet) {
		this.resultSet = resultSet;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
