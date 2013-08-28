package com.ctrip.sysdev.das.serde.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import com.ctrip.sysdev.das.domain.msg.AvailableType;
import com.ctrip.sysdev.das.exception.SerDeException;
import com.ctrip.sysdev.das.serde.MsgPackSerDeType;

public class ChunkSerDe extends AbstractMsgPackSerDe<List<List<AvailableType>>> {

	@Override
	public MsgPackSerDeType getSerDeType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] doSerialize(List<List<AvailableType>> obj)
			throws SerDeException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePack msgpack = new MessagePack();
		Packer packer = msgpack.createPacker(out);
		try {
			packer.writeArrayBegin(obj.size());
			for (List<AvailableType> row : obj) {
				packer.writeArrayBegin(row.size());
				for (AvailableType col : row) {
					packAvailableType(packer, col);
				}
				packer.writeArrayEnd();
			}
			packer.writeArrayEnd();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SerDeException("ResponseSerDe doSerialize exception ", e);
		}
		return out.toByteArray();
	}

	@Override
	public List<List<AvailableType>> doDeserialize(byte[] source)
			throws SerDeException {
		throw new SerDeException("ChunkSerDe not support doDeserialize");
	}

	@Override
	public void accept(Class<?> c) throws SerDeException {
	}

	/**
	 * 
	 * @param packer
	 * @param availableType
	 * @throws Exception
	 */
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

}
