package com.ctrip.sysdev.das.serde.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import com.ctrip.sysdev.das.domain.Response;
import com.ctrip.sysdev.das.domain.enums.ResultTypeEnum;
import com.ctrip.sysdev.das.domain.msg.AvailableType;
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
			if (obj.getResultType() == ResultTypeEnum.RETRIEVE) {
				// means chunk
				packer.write(obj.getChunkCount());
				packer.writeArrayBegin(obj.getResultSet().size());
				for(List<AvailableType> outer : obj.getResultSet()){
					packer.writeArrayBegin(outer.size());
					for(AvailableType inner : outer){
						packAvailableType(packer, inner);
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
	
	private static void packAvailableType(Packer packer,
			AvailableType availableType) throws IOException {

		packer.writeArrayBegin(3);
		packer.write(availableType.paramIndex);
		packer.write(availableType.currentType.getIntVal());
		switch (availableType.currentType) {
		case BOOL:
			packer.write(availableType.bool_arg);
			break;
		case BYTE:
			packer.write(availableType.byte_arg);
			break;
		case SHORT:
			packer.write(availableType.short_arg);
			break;
		case INT:
			packer.write(availableType.int_arg);
			break;
		case LONG:
			packer.write(availableType.long_arg);
			break;
		case FLOAT:
			packer.write(availableType.float_arg);
			break;
		case DOUBLE:
			packer.write(availableType.double_arg);
			break;
		case DECIMAL:
			packer.write(availableType.decimal_arg);
			break;
		case STRING:
			packer.write(availableType.string_arg);
			break;
		case DATETIME:
			packer.write(availableType.datetime_arg);
			break;
		case BYTEARR:
			packer.write(availableType.bytearr_arg);
			break;
		default:
			packer.write(availableType.object_arg);
			break;
		}
		packer.writeArrayEnd();

	}

	private static final int currentPropertyCount = 3;

	@Override
	public Response doDeserialize(byte[] source) throws SerDeException {
		throw new SerDeException("ResponseSerDe not support doDeserialize");
	}

}
