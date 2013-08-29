package com.ctrip.platform.dao.client;

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

import com.ctrip.platform.dao.enums.ActionTypeEnum;
import com.ctrip.platform.dao.enums.FlagsEnum;
import com.ctrip.platform.dao.enums.MessageTypeEnum;
import com.ctrip.platform.dao.enums.ResultTypeEnum;
import com.ctrip.platform.dao.msg.AvailableType;
import com.ctrip.platform.dao.msg.Message;
import com.ctrip.platform.dao.request.DefaultRequest;
import com.ctrip.platform.dao.response.DefaultResponse;
import com.ctrip.platform.dao.utils.Consts;

public class DALClient {
	Socket requestSocket;
	DataOutputStream out;
	DataInputStream in;

	public ResultSet fetch(String tnxCtxt, String statement, int flag,
			AvailableType... params) throws Exception {

		Message message = new Message();

		message.setMessageType(MessageTypeEnum.SQL);
		message.setActionType(ActionTypeEnum.SELECT);
		message.setUseCache(false);

		message.setSql(statement);
		List<List<AvailableType>> finalParams =  new ArrayList<List<AvailableType>>();
		finalParams.add(new ArrayList<AvailableType>(Arrays.asList(params)));
		message.setArgs(finalParams);

		message.setFlags(FlagsEnum.TEST.getIntVal());

		DefaultRequest request = new DefaultRequest();

		request.setTaskid(UUID.randomUUID());

		request.setDbName(Consts.databaseName);

		request.setCredential(Consts.credential);

		request.setMessage(message);

		this.<List<List<AvailableType>>>run(request);

		return null;
	}

	public int execute(String tnxCtxt, String statement, int flag,
			AvailableType... params) throws Exception {

		Message message = new Message();

		message.setMessageType(MessageTypeEnum.SQL);
		message.setActionType(ActionTypeEnum.SELECT);
		message.setUseCache(false);

		message.setSql(statement);
		List<List<AvailableType>> finalParams =  new ArrayList<List<AvailableType>>();
		finalParams.add(new ArrayList<AvailableType>(Arrays.asList(params)));
		message.setArgs(finalParams);


		message.setFlags(FlagsEnum.TEST.getIntVal());

		DefaultRequest request = new DefaultRequest();

		request.setTaskid(UUID.randomUUID());

		request.setDbName(Consts.databaseName);

		request.setCredential(Consts.credential);

		request.setMessage(message);

		this.<Integer>run(request);

		return 0;
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

			if (response.getResultType() == ResultTypeEnum.CUD) {
				//System.out.println("affect row count: "+response.getAffectRowCount());
				return (T) new Integer(response.getAffectRowCount());
			}else{
				
				List<List<AvailableType>> resultSet = new ArrayList<List<AvailableType>>();
				
				for(int i=0;i< response.getChunkCount();i++){
					int currentChunkSize = in.readInt();
					
					byte[] currentChunkData = new byte[currentChunkSize];
					in.read(currentChunkData, 0, currentChunkSize);
					
					resultSet.addAll(DefaultResponse.unpackChunk(currentChunkData));
				}
				
				return (T) resultSet;
				
			}

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
