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

import com.ctrip.platform.dao.enums.AvailableTypeEnum;
import com.ctrip.platform.dao.enums.ResultTypeEnum;
import com.ctrip.platform.dao.exception.ProtocolInvalidException;
import com.ctrip.platform.dao.msg.AvailableType;

public class DefaultResponse extends AbstractResponse {

	private static final int currentPropertyCount = 3;

	private UUID taskid;

	private ResultTypeEnum resultType;

	private int affectRowCount;

	private int chunkCount;

	private List<List<AvailableType>> resultSet;

	public List<List<AvailableType>> getResultSet() {
		return resultSet;
	}

	public void setResultSet(List<List<AvailableType>> resultSet) {
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

		response.setResultType(ResultTypeEnum.fromInt(unpacker.readInt()));

		if (response.getResultType() == ResultTypeEnum.RETRIEVE) {
			response.setChunkCount(unpacker.readInt());

			int outerArrayLength = unpacker.readArrayBegin();
			
			List<List<AvailableType>> outerArray = new ArrayList<List<AvailableType>>(outerArrayLength);

			for (int i = 0; i < outerArrayLength; i++) {
				int innerArrayLength = unpacker.readArrayBegin();
				List<AvailableType> innerArray = new ArrayList<AvailableType>(
						innerArrayLength);

				for (int j = 0; j < innerArrayLength; j++) {
					innerArray.add(unpackAvailableType(unpacker));
				}
				
				outerArray.add(innerArray);

			}
			
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
	public static List<List<AvailableType>> unpackChunk(byte[] payload)
			throws IOException {

		MessagePack packer = new MessagePack();

		ByteArrayInputStream in = new ByteArrayInputStream(payload);
		Unpacker unpacker = packer.createUnpacker(in);

		int propertyCount = unpacker.readArrayBegin();

		List<List<AvailableType>> results = new ArrayList<List<AvailableType>>();

		for (int i = 0; i < propertyCount; i++) {

			List<AvailableType> result = new ArrayList<AvailableType>();

			int columnCount = unpacker.readArrayBegin();

			for (int j = 0; j < columnCount; j++) {
				result.add(unpackAvailableType(unpacker));
			}
			unpacker.readArrayEnd();

		}
		unpacker.readArrayEnd();

		return results;

	}

	/**
	 * 
	 * @param unpacker
	 * @return
	 * @throws IOException
	 */
	private static AvailableType unpackAvailableType(Unpacker unpacker)
			throws IOException {

		AvailableType at = new AvailableType();

		int propertyCount = unpacker.readArrayBegin();

		at.paramIndex = unpacker.readInt();

		at.currentType = AvailableTypeEnum.fromInt(unpacker.readInt());

		switch (at.currentType) {
		case BOOL:
			at.bool_arg = unpacker.readBoolean();
			break;
		case BYTE:
			at.byte_arg = unpacker.readByte();
			break;
		case SHORT:
			at.short_arg = unpacker.readShort();
			break;
		case INT:
			at.int_arg = unpacker.readInt();
			break;
		case LONG:
			at.long_arg = unpacker.readLong();
			break;
		case FLOAT:
			at.float_arg = unpacker.readFloat();
			break;
		case DOUBLE:
			at.double_arg = unpacker.readDouble();
			break;
		case DECIMAL:
			at.decimal_arg = unpacker.read(TBigDecimal);
			break;
		case STRING:
			at.string_arg = unpacker.readString();
			break;
		case DATETIME:
			at.datetime_arg = unpacker.read(Timestamp.class);
			break;
		case BYTEARR:
			at.bytearr_arg = unpacker.readByteArray();
			break;
		default:
			at.object_arg = unpacker.readValue();
		}
		unpacker.readArrayEnd();

		return at;
	}

}
