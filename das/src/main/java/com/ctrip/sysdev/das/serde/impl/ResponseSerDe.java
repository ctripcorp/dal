package com.ctrip.sysdev.das.serde.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import com.ctrip.sysdev.das.domain.Response;
import com.ctrip.sysdev.das.domain.enums.OperationType;
import com.ctrip.sysdev.das.domain.param.StatementParameter;
import com.ctrip.sysdev.das.exception.SerDeException;
import com.ctrip.sysdev.das.serde.MsgPackSerDeType;
import com.ctrip.sysdev.das.utils.UUID2ByteArray;

public class ResponseSerDe extends AbstractMsgPackSerDe<Response> {

	@Override
	public MsgPackSerDeType getSerDeType() {
		return MsgPackSerDeType.RESPONSE_OBJECT;
	}

	@Override
	public void accept(Class<?> c) throws SerDeException {
		if (!Response.class.isAssignableFrom(c)) {
			throw new SerDeException(
					"ResponseSerDe only accept object that implements Response");
		}
	}

	@Override
	public byte[] doSerialize(Response obj) throws SerDeException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePack msgpack = new MessagePack();
		Packer packer = msgpack.createPacker(out);
		try {
			packer.writeArrayBegin(currentPropertyCount);
			packer.write(UUID2ByteArray.asByteArray(obj.getTaskid()));
			packer.write(obj.getResultType().getIntVal());
			if (obj.getResultType() == OperationType.Read) {
				// means chunk
//				packer.write(obj.getChunkCount());
				packer.writeArrayBegin(obj.getResultSet().size());
				for(List<StatementParameter> outer : obj.getResultSet()){
					packer.writeArrayBegin(outer.size());
					for(StatementParameter inner : outer){
						inner.pack(packer);
					}
					packer.writeArrayEnd();
				}
				packer.writeArrayEnd();
			} else {
				packer.write(obj.getAffectRowCount());
			}
			packer.writeArrayEnd();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SerDeException("ResponseSerDe doSerialize exception ", e);
		}
		return out.toByteArray();
	}
	

	private static final int currentPropertyCount = 3;

	@Override
	public Response doDeserialize(byte[] source) throws SerDeException {
		throw new SerDeException("ResponseSerDe not support doDeserialize");
	}

}
