package com.ctrip.sysdev.das.service;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import com.ctrip.sysdev.das.DalService;
import com.ctrip.sysdev.das.domain.Request;
import com.ctrip.sysdev.das.domain.Response;
import com.ctrip.sysdev.das.domain.enums.ResultTypeEnum;
import com.ctrip.sysdev.das.exception.SerDeException;
import com.ctrip.sysdev.das.serde.MsgPackSerDe;
import com.ctrip.sysdev.das.worker.QueryExecutor;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DalServiceImpl extends AbstractDalService implements DalService {

	private MsgPackSerDe<Response> msgPackSerDe;
	private QueryExecutor executor = new QueryExecutor();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject
	public DalServiceImpl(@Named("ResponseSerDe") MsgPackSerDe msgPackSerDe) {
		this.msgPackSerDe = msgPackSerDe;
	}

	/**
	 * @param args
	 */
	public ByteBuf dalService(Request request) {
		this.getDataSourceWrapper();
		
		ByteBuf buf = Unpooled.buffer(10);
		
		Response response = executor.execute(getDataSourceWrapper(), request.getMessage());
		response.setTaskid(request.getTaskid());
		try {
			byte[] msgpack_payload = msgPackSerDe.serialize(response);
			buf.writeInt(msgpack_payload.length + 2);
			buf.writeShort(1);
			buf.writeBytes(msgpack_payload);
		} catch (SerDeException e) {
			e.printStackTrace();
		}

		return buf;
	}
}
