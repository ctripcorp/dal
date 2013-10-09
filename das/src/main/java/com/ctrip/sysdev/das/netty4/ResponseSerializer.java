package com.ctrip.sysdev.das.netty4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import com.ctrip.sysdev.das.domain.Response;
import com.ctrip.sysdev.das.domain.StatementParameter;
import com.ctrip.sysdev.das.domain.enums.OperationType;
import com.ctrip.sysdev.das.exception.SerDeException;
import com.ctrip.sysdev.das.utils.UUID2ByteArray;

public class ResponseSerializer {
	private static final int currentPropertyCount = 3;

	public byte[] serialize(Response resp) throws SerDeException {
		long start = System.currentTimeMillis();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePack msgpack = new MessagePack();
		Packer packer = msgpack.createPacker(out);
		try {
			packer.writeArrayBegin(currentPropertyCount);
			packer.write(UUID2ByteArray.asByteArray(resp.getTaskid()));
			packer.write(resp.getResultType().getIntVal());
			if (resp.getResultType() == OperationType.Read) {
				// means chunk
//				packer.write(obj.getChunkCount());
				packer.writeArrayBegin(resp.getResultSet().size());
				for(List<StatementParameter> outer : resp.getResultSet()){
					packer.writeArrayBegin(outer.size());
					for(StatementParameter inner : outer){
						inner.pack(packer);
					}
					packer.writeArrayEnd();
				}
				packer.writeArrayEnd();
			} else {
				packer.write(resp.getAffectRowCount());
			}
			packer.write(resp.getTotalTime());
			packer.write(resp.getDecodeRequestTime());
			packer.write(resp.getDbTime());
			packer.write(resp.getEncodeResponseTime());
			packer.writeArrayEnd();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SerDeException("ResponseSerDe doSerialize exception ", e);
		}
		
		byte[] bytes = out.toByteArray();
		resp.setEncodeResponseTime(System.currentTimeMillis() - start);
		return bytes;
	}
}
