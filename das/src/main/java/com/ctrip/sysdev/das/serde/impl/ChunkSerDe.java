package com.ctrip.sysdev.das.serde.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import com.ctrip.sysdev.das.domain.StatementParameter;
import com.ctrip.sysdev.das.exception.SerDeException;
import com.ctrip.sysdev.das.serde.MsgPackSerDeType;

public class ChunkSerDe extends AbstractMsgPackSerDe<List<List<StatementParameter>>> {

	@Override
	public MsgPackSerDeType getSerDeType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] doSerialize(List<List<StatementParameter>> obj)
			throws SerDeException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePack msgpack = new MessagePack();
		Packer packer = msgpack.createPacker(out);
		try {
			packer.writeArrayBegin(obj.size());
			for (List<StatementParameter> row : obj) {
				packer.writeArrayBegin(row.size());
				for (StatementParameter col : row) {
					col.pack(packer);
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
	public List<List<StatementParameter>> doDeserialize(byte[] source)
			throws SerDeException {
		throw new SerDeException("ChunkSerDe not support doDeserialize");
	}

	@Override
	public void accept(Class<?> c) throws SerDeException {
	}


}
