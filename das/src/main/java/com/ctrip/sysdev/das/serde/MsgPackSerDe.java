package com.ctrip.sysdev.das.serde;

import com.ctrip.sysdev.das.exception.SerDeException;


/**
 * 
 * @author weiw
 * 
 */
public interface MsgPackSerDe<T> {
	/**
	 * 
	 * @param object
	 * @return
	 * @throws SerDeException
	 */
	public  byte[] serialize(T object) throws SerDeException;

	/**
	 * 
	 * @param data
	 * @param deserializeClass
	 * @return
	 * @throws SerDeException
	 */
	public   T deserialize(byte[] data) throws SerDeException;

	/**
	 * 
	 * @return
	 */
	public MsgPackSerDeType getSerDeType();
}