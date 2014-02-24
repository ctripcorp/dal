package com.ctrip.platform.dal.dao.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.client.DasProto.CRUD;
import com.ctrip.platform.dal.dao.client.DasProto.StatementType;

/**
 * TODO support exception report
 * @author jhhe
 *
 */
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
	
		long encodeStart = System.currentTimeMillis();
		
		PooledSocket sock = pool.acquire();

		if (sock != null) {
			byte[] payload = request.toByteArray();

			sock.getOut().writeInt(2 + payload.length);
			sock.getOut().writeShort(1);
			sock.getOut().write(payload);
		}
		
		long encodeEnd = System.currentTimeMillis();

		TimeCostSender.getInstance().getQueue().add(
				String.format("values=%s:encodeRequestTime:%d;", request.getId(), encodeEnd - encodeStart));
		
		return sock;
	}

	private DasProto.Response readResponse(PooledSocket sock)
			throws IOException {
		
		long decodeStart = System.currentTimeMillis();
		
		int totalLength = sock.getIn().readInt();
		short version = sock.getIn().readShort();
		byte[] data = new byte[totalLength - 2];

		sock.getIn().readFully(data);
		DasProto.Response response =  DasProto.Response.parseFrom(data);
		
		long decodeEnd = System.currentTimeMillis();
		
		if(response.getResultType() == CRUD.CUD){
			TimeCostSender.getInstance().getQueue().add(
					String.format("values=%s:decodeResponseTime:%d;", response.getId(), decodeEnd - decodeStart));
			
			TimeCostSender.getInstance().getQueue().add(
					String.format("values=%s:totalCount:%d;", response.getId(), response.getAffectRows()));
			
			TimeCostSender.getInstance().getQueue().add(
					String.format("values=%s:totalBytes:%d;", response.getId(), totalLength));
		}
		
		return response;
	}

	@Override
	public ResultSet fetch(String sql, List<StatementParameter> parameters, Map keywordParameters) {

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
			resultSet.setCurrentId(response.getId());

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
	public int execute(String sql, List<StatementParameter> parameters, Map keywordParameters) {
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
	public ResultSet fetchBySp(String sql, List<StatementParameter> parameters, Map keywordParameters) {
		boolean master = false;

		if (null != keywordParameters && keywordParameters.size() > 0
				&& keywordParameters.containsKey("master")) {
			master = Boolean.getBoolean(keywordParameters.get("master")
					.toString());
		}

		DasProto.RequestMessage.Builder msgBuilder = DasProto.RequestMessage
				.newBuilder();

		msgBuilder.setStateType(StatementType.SP).setCrud(CRUD.GET)
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
	public int executeSp(String sql, List<StatementParameter> parameters, Map keywordParameters) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void closeConnection() {
		// For DAS, we do nothing for now
	}
}
