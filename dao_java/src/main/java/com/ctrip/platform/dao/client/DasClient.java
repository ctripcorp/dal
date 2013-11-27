package com.ctrip.platform.dao.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ctrip.platform.dao.DasProto;
import com.ctrip.platform.dao.DasProto.CRUD;
import com.ctrip.platform.dao.DasProto.StatementType;
import com.ctrip.platform.dao.param.StatementParameter;

public class DasClient implements Client {

	private String logicDbName;

	private String credentialId;

	private SocketPool pool;

	public void init(String host, int port) {
		pool = new SocketPool(host, port);
	}

	public String getLogicDbName() {
		return logicDbName;
	}

	public void setLogicDbName(String logicDbName) {
		this.logicDbName = logicDbName;
	}

	public String getCredentialId() {
		return credentialId;
	}

	public void setCredentialId(String credentialId) {
		this.credentialId = credentialId;
	}

	private PooledSocket writeRequest(DasProto.Request request)
			throws UnknownHostException, IOException {
		PooledSocket sock = pool.acquire();

		if (sock != null) {
			byte[] payload = request.toByteArray();

			sock.getOut().writeInt(2 + payload.length);
			sock.getOut().writeShort(1);
			sock.getOut().write(payload);
		}

		return sock;
	}

	private DasProto.Response readResponse(PooledSocket sock)
			throws IOException {
		int totalLength = sock.getIn().readInt();
		short version = sock.getIn().readShort();
		byte[] data = new byte[totalLength - 2];

		sock.getIn().readFully(data);
		return DasProto.Response.parseFrom(data);
	}

	@Override
	public ResultSet fetch(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {

		boolean master = false;

		if (null != keywordParameters && keywordParameters.size() > 0
				&& keywordParameters.containsKey("master")) {
			master = Boolean.getBoolean(keywordParameters.get("master")
					.toString());
		}

		DasProto.RequestMessage.Builder msgBuilder = DasProto.RequestMessage
				.newBuilder();

		msgBuilder.setStateType(StatementType.SQL).setCrud(CRUD.GET)
				.setFlags(1).setMaster(master).setName(sql);

		if (null != parameters) {
			for (StatementParameter p : parameters) {
				msgBuilder.addParameters(p.build2SqlParameters());
			}
		}

		DasProto.Request.Builder requestBuilder = DasProto.Request.newBuilder();

		UUID taskid = UUID.randomUUID();

		requestBuilder.setMsg(msgBuilder.build()).setCred(credentialId)
				.setDb(logicDbName).setId(taskid.toString());

		try {
			PooledSocket sock = writeRequest(requestBuilder.build());
			DasProto.Response response = readResponse(sock);

			DasResultSet resultSet = new DasResultSet();
			resultSet.setHeader(response.getHeaderList());
			resultSet.setSocket(sock);

			return resultSet;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public int execute(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		boolean master = false;

		if (null != keywordParameters && keywordParameters.size() > 0
				&& keywordParameters.containsKey("master")) {
			master = Boolean.getBoolean(keywordParameters.get("master")
					.toString());
		}

		DasProto.RequestMessage.Builder msgBuilder = DasProto.RequestMessage
				.newBuilder();

		msgBuilder.setStateType(StatementType.SQL).setCrud(CRUD.CUD)
				.setFlags(1).setMaster(master).setName(sql);

		if (null != parameters) {
			for (StatementParameter p : parameters) {
				msgBuilder.addParameters(p.build2SqlParameters());
			}
		}

		DasProto.Request.Builder requestBuilder = DasProto.Request.newBuilder();

		UUID taskid = UUID.randomUUID();

		requestBuilder.setMsg(msgBuilder.build()).setCred(credentialId)
				.setDb(logicDbName).setId(taskid.toString());

		try {
			PooledSocket sock = writeRequest(requestBuilder.build());
			DasProto.Response response = readResponse(sock);
			
			sock.recycle(null);
			
			return response.getAffectRows();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	@Override
	public ResultSet fetchBySp(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int executeSp(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		// TODO Auto-generated method stub
		return 0;
	}

}
