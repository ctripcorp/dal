package com.ctrip.sysdev.das.netty4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import com.ctrip.sysdev.das.domain.StatementParameter;
import com.ctrip.sysdev.das.exception.SerDeException;

public class ChunkSerializer {

	public byte[] serialize(List<List<StatementParameter>> obj)
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
}
