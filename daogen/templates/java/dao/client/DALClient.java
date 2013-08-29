package {{product_line}}.{{domain}}.{{app_name}}.dao.client;

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

import {{product_line}}.{{domain}}.{{app_name}}.dao.enums.ActionTypeEnum;
import {{product_line}}.{{domain}}.{{app_name}}.dao.enums.FlagsEnum;
import {{product_line}}.{{domain}}.{{app_name}}.dao.enums.MessageTypeEnum;
import {{product_line}}.{{domain}}.{{app_name}}.dao.enums.ResultTypeEnum;
import {{product_line}}.{{domain}}.{{app_name}}.dao.msg.AvailableType;
import {{product_line}}.{{domain}}.{{app_name}}.dao.msg.Message;
import {{product_line}}.{{domain}}.{{app_name}}.dao.request.DefaultRequest;
import {{product_line}}.{{domain}}.{{app_name}}.dao.response.DefaultResponse;
import {{product_line}}.{{domain}}.{{app_name}}.dao.utils.Consts;

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

		run(request);

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

		run(request);

		return 0;
	}

	void run(DefaultRequest request) {
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
				System.out.println("affect row count: "+response.getAffectRowCount());
			}else{
				
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
	}

	public static void main(String[] args) {
		new DALClient();
	}

}
