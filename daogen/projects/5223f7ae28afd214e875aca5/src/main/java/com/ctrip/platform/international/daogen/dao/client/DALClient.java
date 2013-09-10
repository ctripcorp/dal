package com.ctrip.platform.international.daogen.dao.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.ctrip.platform.international.daogen.dao.enums.ActionTypeEnum;
import com.ctrip.platform.international.daogen.dao.enums.FlagsEnum;
import com.ctrip.platform.international.daogen.dao.enums.MessageTypeEnum;
import com.ctrip.platform.international.daogen.dao.enums.ResultTypeEnum;
import com.ctrip.platform.international.daogen.dao.param.Parameter;
import com.ctrip.platform.international.daogen.dao.request.DefaultRequest;
import com.ctrip.platform.international.daogen.dao.request.RequestMessage;
import com.ctrip.platform.international.daogen.dao.response.DefaultResponse;
import com.ctrip.platform.international.daogen.dao.utils.Consts;
import com.ctrip.platform.international.daogen.dao.utils.DAOResultSet;

public class DALClient {
	Socket requestSocket;
	DataOutputStream out;
	DataInputStream in;

	public ResultSet fetch(String tnxCtxt, String statement, int flag,
			Parameter... params) throws Exception {

		RequestMessage message = new RequestMessage();

		message.setMessageType(MessageTypeEnum.SQL);
		message.setActionType(ActionTypeEnum.SELECT);
		message.setUseCache(false);

		message.setSql(statement);
		List<List<Parameter>> finalParams =  new ArrayList<List<Parameter>>();
		finalParams.add(new ArrayList<Parameter>(Arrays.asList(params)));
		message.setArgs(finalParams);

		message.setFlags(FlagsEnum.TEST.getIntVal());

		DefaultRequest request = new DefaultRequest();

		request.setTaskid(UUID.randomUUID());

		request.setDbName(Consts.databaseName);

		request.setCredential(Consts.credential);

		request.setMessage(message);
		
		DAOResultSet rs = new DAOResultSet(this.<List<List<Parameter>>>run(request));
		
		return rs;

//		return null;
	}
	
	public ResultSet fetchBySp(String tnxCtxt, String sp, int flag,
			Parameter... params) throws Exception {
		
		RequestMessage msg = new RequestMessage();

		msg.setMessageType(MessageTypeEnum.SP);
		msg.setActionType(ActionTypeEnum.SELECT);
		msg.setUseCache(false);
		
		msg.setSpName(sp);
		
		List<List<Parameter>> finalParams =  new ArrayList<List<Parameter>>();
		finalParams.add(new ArrayList<Parameter>(Arrays.asList(params)));
		msg.setArgs(finalParams);

		msg.setFlags(FlagsEnum.TEST.getIntVal());

		DefaultRequest request = new DefaultRequest();

		request.setTaskid(UUID.randomUUID());

		request.setDbName(Consts.databaseName);

		request.setCredential(Consts.credential);

		request.setMessage(msg);
		
		DAOResultSet rs = new DAOResultSet(this.<List<List<Parameter>>>run(request));
		
		return rs;
	}

	public int execute(String tnxCtxt, String statement, int flag,
			Parameter... params) throws Exception {

		RequestMessage message = new RequestMessage();

		message.setMessageType(MessageTypeEnum.SQL);
		message.setActionType(ActionTypeEnum.DELETE);
		message.setUseCache(false);

		message.setSql(statement);
		List<List<Parameter>> finalParams =  new ArrayList<List<Parameter>>();
		finalParams.add(new ArrayList<Parameter>(Arrays.asList(params)));
		message.setArgs(finalParams);


		message.setFlags(FlagsEnum.TEST.getIntVal());

		DefaultRequest request = new DefaultRequest();

		request.setTaskid(UUID.randomUUID());

		request.setDbName(Consts.databaseName);

		request.setCredential(Consts.credential);

		request.setMessage(message);

		return this.<Integer>run(request);

//		return 0;
	}
	
	public int executeSp(String tnxCtxt, String sp, int flag,
			Parameter... params) throws Exception {
		
		RequestMessage message = new RequestMessage();

		message.setMessageType(MessageTypeEnum.SP);
		message.setActionType(ActionTypeEnum.DELETE);
		message.setUseCache(false);

		message.setSpName(sp);
		List<List<Parameter>> finalParams =  new ArrayList<List<Parameter>>();
		finalParams.add(new ArrayList<Parameter>(Arrays.asList(params)));
		message.setArgs(finalParams);


		message.setFlags(FlagsEnum.TEST.getIntVal());

		DefaultRequest request = new DefaultRequest();

		request.setTaskid(UUID.randomUUID());

		request.setDbName(Consts.databaseName);

		request.setCredential(Consts.credential);

		request.setMessage(message);

		return this.<Integer>run(request);

	}

	<T> T run(DefaultRequest request) {
		try {
			// 1. creating a socket to connect to the server
			requestSocket = new Socket("localhost", 9000);

			// 2. get Input and Output streams
			out = new DataOutputStream(requestSocket.getOutputStream());

			in = new DataInputStream(requestSocket.getInputStream());
			// 3: Communicating with the server

			byte[] payload = request.packToByteArray();
			
			out.writeInt(payload.length + 2);

			out.writeShort(Consts.protocolVersion);

			out.write(payload, 0, payload.length);

			out.flush();
			
			int leftLength = in.readInt();

			short protocolVersion = in.readShort();
			
			byte[] leftData = new byte[leftLength - 2];
			
			in.read(leftData, 0, leftLength - 2);
			
			DefaultResponse response = DefaultResponse.unpack(leftData);
			
			if(response.getResultType() == ResultTypeEnum.CUD){
				return (T) new Integer(response.getAffectRowCount());
			}else{
				return (T) response.getResultSet();
			}

//			if (response.getResultType() == ResultTypeEnum.CUD) {
//				//System.out.println("affect row count: "+response.getAffectRowCount());
//				return (T) new Integer(response.getAffectRowCount());
//			}else{
//				
//				List<List<Parameter>> resultSet = new ArrayList<List<Parameter>>();
//				
//				for(int i=0;i< response.getChunkCount();i++){
//					int currentChunkSize = in.readInt();
//					
//					byte[] currentChunkData = new byte[currentChunkSize];
//					in.read(currentChunkData, 0, currentChunkSize);
//					
//					resultSet.addAll(DefaultResponse.unpackChunk(currentChunkData));
//				}
//				
//				return (T) resultSet;
//				
//			}

		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// 4: Closing connection
			try {
				out.close();
				in.close();
				requestSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
		
		return null;
	}

	public static void main(String[] args) {
		new DALClient();
	}

}
