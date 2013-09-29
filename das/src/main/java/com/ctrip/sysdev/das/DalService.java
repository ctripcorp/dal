package com.ctrip.sysdev.das;

import java.sql.Connection;
import java.sql.SQLException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import com.ctrip.sysdev.das.commons.DataSourceWrapper;
import com.ctrip.sysdev.das.domain.Request;
import com.ctrip.sysdev.das.domain.Response;
import com.ctrip.sysdev.das.exception.SerDeException;
import com.ctrip.sysdev.das.serde.MsgPackSerDe;
import com.ctrip.sysdev.das.worker.QueryExecutor;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DalService {

	private MsgPackSerDe<Response> msgPackSerDe;
	private QueryExecutor executor = new QueryExecutor();

	@Inject
	private DataSourceWrapper dataSourceWrapper;

	public Connection getConnection() throws SQLException {
		return dataSourceWrapper.getConnection();
	}

	public DataSourceWrapper getDataSourceWrapper() {
		return dataSourceWrapper;
	}

	public void setDataSourceWrapper(DataSourceWrapper dataSourceWrapper) {
		this.dataSourceWrapper = dataSourceWrapper;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject
	public DalService(@Named("ResponseSerDe") MsgPackSerDe msgPackSerDe) {
		this.msgPackSerDe = msgPackSerDe;
	}

	/**
	 * @param args
	 */
	public ByteBuf dalService(Request request) {
		this.getDataSourceWrapper();

		ByteBuf buf = Unpooled.buffer();

		
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
