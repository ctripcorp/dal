package com.ctrip.platform.dao.response;

import static org.msgpack.template.Templates.TBigDecimal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.msgpack.MessagePack;
import org.msgpack.unpacker.Unpacker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dao.enums.ResultTypeEnum;
import com.ctrip.platform.dao.exception.ProtocolInvalidException;
import com.ctrip.platform.dao.param.Parameter;
import com.ctrip.platform.dao.param.ParameterFactory;

public class DefaultResponse extends AbstractResponse {
	
	private static Logger logger = LoggerFactory.getLogger(DefaultResponse.class);

	private static final int currentPropertyCount = 3;

	private UUID taskid;

	private ResultTypeEnum resultType;

	private int affectRowCount;

	private int chunkCount;

	private List<List<Parameter>> resultSet;

	public List<List<Parameter>> getResultSet() {
		return resultSet;
	}

	public void setResultSet(List<List<Parameter>> resultSet) {
		this.resultSet = resultSet;
	}

	public UUID getTaskid() {
		return taskid;
	}

	public void setTaskid(UUID taskid) {
		this.taskid = taskid;
	}

	public ResultTypeEnum getResultType() {
		return resultType;
	}

	public void setResultType(ResultTypeEnum resultType) {
		this.resultType = resultType;
	}

	public int getAffectRowCount() {
		return affectRowCount;
	}

	public void setAffectRowCount(int affectRowCount) {
		this.affectRowCount = affectRowCount;
	}

	public int getChunkCount() {
		return chunkCount;
	}

	public void setChunkCount(int chunkCount) {
		this.chunkCount = chunkCount;
	}

	/**
	 * Pack the response object for socket write
	 * 
	 * @return
	 * @throws ProtocolInvalidException
	 * @throws Exception
	 */
	public static DefaultResponse unpack(byte[] payload) throws IOException,
			ProtocolInvalidException {

		MessagePack packer = new MessagePack();

		// The object to return
		DefaultResponse response = new DefaultResponse();

		ByteArrayInputStream in = new ByteArrayInputStream(payload);
		Unpacker unpacker = packer.createUnpacker(in);

		int propertyCount = unpacker.readArrayBegin();

		// Property count invalid
		if (propertyCount != currentPropertyCount) {
			throw new ProtocolInvalidException(String.format(
					"Expect property count %d, but got %d instead!",
					currentPropertyCount, propertyCount));
		}

		response.setTaskid(UUID.nameUUIDFromBytes(unpacker.readByteArray()));
		
		logger.info(response.getTaskid().toString());

		response.setResultType(ResultTypeEnum.fromInt(unpacker.readInt()));

		if (response.getResultType() == ResultTypeEnum.RETRIEVE) {
//			response.setChunkCount(unpacker.readInt());

			int outerArrayLength = unpacker.readArrayBegin();
			
			List<List<Parameter>> outerArray = new ArrayList<List<Parameter>>(outerArrayLength);

			for (int i = 0; i < outerArrayLength; i++) {
				int innerArrayLength = unpacker.readArrayBegin();
				List<Parameter> innerArray = new ArrayList<Parameter>(
						innerArrayLength);

				for (int j = 0; j < innerArrayLength; j++) {
					innerArray.add(ParameterFactory.createParameterFromUnpack(unpacker));
				}
				unpacker.readArrayEnd();
				
				outerArray.add(innerArray);

			}
			unpacker.readArrayEnd();
			
			response.setResultSet(outerArray);

		} else {
			response.setAffectRowCount(unpacker.readInt());
		}

		unpacker.readArrayEnd();

		unpacker.close();

		return response;
	}

	/**
	 * Pack a chunk of data, some records of data
	 * 
	 * @param chunk
	 * @return
	 * @throws IOException
	 */
	public static List<List<Parameter>> unpackChunk(byte[] payload)
			throws IOException {

		MessagePack packer = new MessagePack();

		ByteArrayInputStream in = new ByteArrayInputStream(payload);
		Unpacker unpacker = packer.createUnpacker(in);

		int propertyCount = unpacker.readArrayBegin();

		List<List<Parameter>> results = new ArrayList<List<Parameter>>();

		for (int i = 0; i < propertyCount; i++) {

			List<Parameter> result = new ArrayList<Parameter>();

			int columnCount = unpacker.readArrayBegin();

			for (int j = 0; j < columnCount; j++) {
				result.add(ParameterFactory.createParameterFromUnpack(unpacker));
			}
			unpacker.readArrayEnd();

		}
		unpacker.readArrayEnd();

		return results;

	}



}
