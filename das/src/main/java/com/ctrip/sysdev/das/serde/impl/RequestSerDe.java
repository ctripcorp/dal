package com.ctrip.sysdev.das.serde.impl;

import static org.msgpack.template.Templates.TBigDecimal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.msgpack.unpacker.Unpacker;

import com.ctrip.sysdev.das.domain.Request;
import com.ctrip.sysdev.das.domain.enums.ActionTypeEnum;
import com.ctrip.sysdev.das.domain.enums.AvailableTypeEnum;
import com.ctrip.sysdev.das.domain.enums.MessageTypeEnum;
import com.ctrip.sysdev.das.domain.msg.AvailableType;
import com.ctrip.sysdev.das.domain.msg.Message;
import com.ctrip.sysdev.das.exception.ProtocolInvalidException;
import com.ctrip.sysdev.das.exception.SerDeException;
import com.ctrip.sysdev.das.serde.MsgPackSerDeType;

public class RequestSerDe extends AbstractMsgPackSerDe<Request> {

	@Override
	public MsgPackSerDeType getSerDeType() {
		return MsgPackSerDeType.REQUEST_OBJECT;
	}

	@Override
	public void accept(Class<?> c) throws SerDeException {
		if (!Request.class.isAssignableFrom(c)) {
			throw new SerDeException(
					"RequestObjectSerDe only accept object that implements Request");
		}
	}

	@Override
	public byte[] doSerialize(Request obj) throws SerDeException {
		throw new SerDeException("RequestObjectSerDe not support doSerialize");
	}

	@Override
	public Request doDeserialize(byte[] source) throws SerDeException {
		Request request = Request.getNewInstance();
		try {
			MessagePack packer = new MessagePack();
			// The object to return
			ByteArrayInputStream in = new ByteArrayInputStream(source);
			Unpacker unpacker = packer.createUnpacker(in);
			int propertyCount = unpacker.readArrayBegin();

			// Property count invalid
			if (propertyCount != currentPropertyCount) {
				throw new ProtocolInvalidException(String.format(
						"Expect property count %d, but got %d instead!",
						currentPropertyCount, propertyCount));
			}
			request.setTaskid(UUID.nameUUIDFromBytes(unpacker.readByteArray()));
			request.setDbName(unpacker.readString());
			request.setCredential(unpacker.readString());
			request.setMessage(unpackMessage(unpacker));
			unpacker.readArrayEnd();
			unpacker.close();

		} catch (IOException e) {
			e.printStackTrace();
			throw new SerDeException("RequestObjectSerDe  doDeserialize error",
					e);
		} catch (ProtocolInvalidException e) {
			e.printStackTrace();
			throw new SerDeException("RequestObjectSerDe  doDeserialize error",
					e);
		}
		return request;
	}

	private static final int currentPropertyCount = 4;

	/**
	 * 
	 * @param unpacker
	 * @return
	 * @throws IOException
	 * @throws ProtocolInvalidException
	 */
	private static Message unpackMessage(Unpacker unpacker) throws IOException,
			ProtocolInvalidException {

		int propertyCount = unpacker.readArrayBegin();

		Message message = new Message();

		message.setMessageType(MessageTypeEnum.fromInt(unpacker.readInt()));

		message.setActionType(ActionTypeEnum.fromInt(unpacker.readInt()));

		message.setUseCache(unpacker.readBoolean());

		if (message.getMessageType() == MessageTypeEnum.SP) {
			message.setSpName(unpacker.readString());
		} else {
			message.setSql(unpacker.readString());
		}

		int argsLength = unpacker.readArrayBegin();
		message.setArgs(new ArrayList<List<AvailableType>>(argsLength));

		for (int i = 0; i < argsLength; i++) {

			int argLength = unpacker.readArrayBegin();
			List<AvailableType> arg = new ArrayList<AvailableType>(argLength);
			for (int j = 0; j < argLength; j++) {
				arg.add(unpackAvailableType(unpacker));
			}
			unpacker.readArrayEnd();
			message.getArgs().add(arg);
		}

		unpacker.readArrayEnd();

		message.setFlags(unpacker.readInt());

		return message;
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
			Value v = unpacker.readValue();
			if (v.isArrayValue()) {
				at.object_arg = v.asArrayValue().toArray();
			} else {
				at.object_arg = v;
			}
		}
		unpacker.readArrayEnd();

		return at;
	}
}
