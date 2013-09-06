package com.ctrip.sysdev.das.serde.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.msgpack.MessagePack;
import org.msgpack.unpacker.Unpacker;

import com.ctrip.sysdev.das.domain.Request;
import com.ctrip.sysdev.das.domain.RequestMessage;
import com.ctrip.sysdev.das.domain.enums.ActionTypeEnum;
import com.ctrip.sysdev.das.domain.enums.MessageTypeEnum;
import com.ctrip.sysdev.das.domain.param.Parameter;
import com.ctrip.sysdev.das.domain.param.ParameterFactory;
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
	private static RequestMessage unpackMessage(Unpacker unpacker) throws IOException,
			ProtocolInvalidException {

		int propertyCount = unpacker.readArrayBegin();

		RequestMessage message = new RequestMessage();

		message.setMessageType(MessageTypeEnum.fromInt(unpacker.readInt()));

		message.setActionType(ActionTypeEnum.fromInt(unpacker.readInt()));

		message.setUseCache(unpacker.readBoolean());

		if (message.getMessageType() == MessageTypeEnum.SP) {
			message.setSpName(unpacker.readString());
		} else {
			message.setSql(unpacker.readString());
		}

		int argsLength = unpacker.readArrayBegin();
		message.setArgs(new ArrayList<List<Parameter>>(argsLength));

		for (int i = 0; i < argsLength; i++) {

			int argLength = unpacker.readArrayBegin();
			List<Parameter> arg = new ArrayList<Parameter>(argLength);
			for (int j = 0; j < argLength; j++) {
				arg.add(ParameterFactory.createParameterFromUnpack(unpacker));
			}
			unpacker.readArrayEnd();
			message.getArgs().add(arg);
		}

		unpacker.readArrayEnd();

		message.setFlags(unpacker.readInt());

		return message;
	}

	
}
