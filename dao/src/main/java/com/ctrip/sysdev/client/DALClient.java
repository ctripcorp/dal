package com.ctrip.sysdev.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.ctrip.sysdev.dao.DAOFunction;
import com.ctrip.sysdev.enums.ActionType;
import com.ctrip.sysdev.enums.AvailableTypeEnum;
import com.ctrip.sysdev.enums.MessageType;
import com.ctrip.sysdev.msg.AvailableType;
import com.ctrip.sysdev.msg.MessageObject;
import com.ctrip.sysdev.pack.MessageObjectPacker;
import com.ctrip.sysdev.utils.Consts;

public class DALClient {
	Socket requestSocket;
	DataOutputStream out;
 	DataInputStream in;
 	
 	public ResultSet fetch(String tnxCtxt, DAOFunction statement, 
			List<AvailableType> params, int flag) throws Exception{
		
		
		
		return null;
 	}

	void run()
	{
		try{
			//1. creating a socket to connect to the server
			requestSocket = new Socket("localhost", 9000);
			
			
			//2. get Input and Output streams
			out = new DataOutputStream(requestSocket.getOutputStream());
			
			in = new DataInputStream(requestSocket.getInputStream());
			//3: Communicating with the server
			
			MessageObject mo = new MessageObject();
			
			mo.messageType = MessageType.SQL;
			mo.actionType = ActionType.INSERT;
			mo.useCache = false;
//			mo.SPName = "what";
//			mo.SPKVParams = new HashMap<String, AvailableType>();
//			mo.BulkSQL = new HashMap<String, List<AvailableType>>();
			
//			AvailableType at = new AvailableType();
//			at.currentType = AvailableTypeEnum.BOOL;
//			at.bool_arg = true;
//			
//			mo.SPKVParams.put("hello", at);
			
			byte[] payload = new MessageObjectPacker().pack(mo);
			
			int wholeLength = 4 + 16 + 2 + 2 + 2 + 4 
					 + Consts.credential.length() + payload.length;
			
//			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//			
//			outStream.write(b);
			
			out.writeInt(wholeLength);
			
			System.out.println(out.size());
			
			byte[] uuid = asByteArray(UUID.randomUUID());
			
			out.write(uuid, 0, uuid.length);
			
			System.out.println(out.size());
			
			out.writeShort(Consts.databaseId);
			
			System.out.println(out.size());
			
			out.writeShort(Consts.credential.length());
			
			out.writeBytes(Consts.credential);
			
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
	
	private  byte[] asByteArray(UUID uuid) 
	 {
	    long msb = uuid.getMostSignificantBits();
	    long lsb = uuid.getLeastSignificantBits();
	    byte[] buffer = new byte[16];

	    for (int i = 0; i < 8; i++) {
	            buffer[i] = (byte) (msb >>> 8 * (7 - i));
	    }
	    for (int i = 8; i < 16; i++) {
	            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
	    }

	    return buffer;

	}
	
	public static void main(String[] args) {
		new DALClient().run();
	}
	

}
