package com.ctrip.sysdev.apptools.dao.pack;

import static org.msgpack.template.Templates.TBigDecimal;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.unpacker.Unpacker;

import com.ctrip.sysdev.apptools.dao.enums.AvailableTypeEnum;
import com.ctrip.sysdev.apptools.dao.enums.ResultType;
import com.ctrip.sysdev.apptools.dao.msg.AvailableType;
import com.ctrip.sysdev.apptools.dao.msg.ResponseObject;

public class ResponseObjectUnPacker {

	public static ResponseObject unpack(byte[] payload) throws Exception {

		MessagePack packer = new MessagePack();

		// The object to return
		ResponseObject result = new ResponseObject();

		ByteArrayInputStream in = new ByteArrayInputStream(payload);
		Unpacker unpacker = packer.createUnpacker(in);

		// The count of the object properties
		int propertyCount = unpacker.readArrayBegin();

		// The first if an integer, 0 represents SQL, 1 represents SP
		result.resultType = ResultType.fromInt(unpacker.readInt());

		// Write the information of sp
		if (result.resultType == ResultType.CUD) {

			result.affectRowCount = unpacker.readInt();

		} else {
			result.chunkCount = unpacker.readInt();
			result.recordPerChunk = unpacker.readInt();

		}

		unpacker.readArrayEnd();

		return result;
	}

	public static List<AvailableType> unpackChunk(byte[] payload) throws Exception {

		MessagePack packer = new MessagePack();
		ByteArrayInputStream in = new ByteArrayInputStream(payload);
		Unpacker unpacker = packer.createUnpacker(in);

		// The count of the object properties
		int propertyCount = unpacker.readArrayBegin();

		List<AvailableType> results = new ArrayList<AvailableType>(
				propertyCount);

		for (int i = 0; i < propertyCount; i++) {
			results.add(unpackAvailableType(unpacker));
		}

		unpacker.readArrayEnd();

		return results;

	}

	/**
	 * Convert AvailableType to MsgPack format
	 * 
	 * @param packer
	 * @param availableType
	 * @throws Exception
	 */
	private static AvailableType unpackAvailableType(Unpacker unpacker)
			throws Exception {

		AvailableType at = new AvailableType();

		int propertyCount = unpacker.readArrayBegin();

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
