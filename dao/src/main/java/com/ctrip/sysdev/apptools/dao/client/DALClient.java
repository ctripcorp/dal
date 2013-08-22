package com.ctrip.sysdev.apptools.dao.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import com.ctrip.sysdev.apptools.dao.enums.ActionType;
import com.ctrip.sysdev.apptools.dao.enums.Flags;
import com.ctrip.sysdev.apptools.dao.enums.MessageType;
import com.ctrip.sysdev.apptools.dao.msg.AvailableType;
import com.ctrip.sysdev.apptools.dao.msg.MessageObject;
import com.ctrip.sysdev.apptools.dao.msg.RequestObject;
import com.ctrip.sysdev.apptools.dao.pack.RequestObjectPacker;
import com.ctrip.sysdev.apptools.dao.utils.Consts;
import com.ctrip.sysdev.apptools.dao.utils.UUID2ByteArray;

public class DALClient {
	Socket requestSocket;
	DataOutputStream out;
 	DataInputStream in;
 	
 	public ResultSet fetch(String tnxCtxt, String statement, int flag, 
			AvailableType... params) throws Exception{
		
		MessageObject message = new MessageObject();
		
		message.messageType = MessageType.SQL;
		message.actionType = ActionType.SELECT;
		message.useCache = false;
		
		message.batchOperation = false;
		message.SQL = statement;
		message.singleArgs = new ArrayList<AvailableType>(
				Arrays.asList(params));
		
		message.flags = Flags.TEST.getIntVal();
		
		RequestObject request = new RequestObject();
		
		request.taskid = UUID.randomUUID();
		
		request.dbName = Consts.databaseName;
		
		request.credential = Consts.credential;
		
		request.message = message;
		
		run(request);
		
		
		return null;
 	}

	void run(RequestObject request)
	{
		try{
			//1. creating a socket to connect to the server
			requestSocket = new Socket("localhost", 9000);
			
			
			//2. get Input and Output streams
			out = new DataOutputStream(requestSocket.getOutputStream());
			
			in = new DataInputStream(requestSocket.getInputStream());
			//3: Communicating with the server
			
			byte[] payload = RequestObjectPacker.pack(request);
			
			out.writeShort(Consts.protocolVersion);
			
			out.writeInt(payload.length);
			
			out.write(payload, 0, payload.length);
			
			out.flush();
			
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				out.close();
				in.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args) {
		new DALClient();
	}
	

}
