package com.ctrip.sysdev.das.domain;

import java.util.List;
import java.util.UUID;

import org.msgpack.packer.Packer;

import com.ctrip.sysdev.das.domain.enums.ResultTypeEnum;
import com.ctrip.sysdev.das.domain.msg.AvailableType;

public class Response extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3910719585739700494L;

	private static Packer packer;

	private UUID taskid;

	private ResultTypeEnum resultType;

	private int affectRowCount;

	private List<List<AvailableType>> resultSet;
	

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
	public ResultTypeEnum getResultType() {
		return resultType;
	}

	/**
	 * @param resultType
	 *            the resultType to set
	 */
	public void setResultType(ResultTypeEnum resultType) {
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
	public List<List<AvailableType>> getResultSet() {
		return resultSet;
	}

	/**
	 * @param resultSet
	 *            the resultSet to set
	 */
	public void setResultSet(List<List<AvailableType>> resultSet) {
		this.resultSet = resultSet;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
