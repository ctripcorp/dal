package com.ctrip.sysdev.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import com.ctrip.sysdev.enums.ActionType;
import com.ctrip.sysdev.enums.Flags;
import com.ctrip.sysdev.enums.MessageType;
import com.ctrip.sysdev.msg.AvailableType;
import com.ctrip.sysdev.msg.MessageObject;
import com.ctrip.sysdev.pack.MessageObjectPacker;
import com.ctrip.sysdev.utils.Consts;

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
		
		run(message);
		
		
		return null;
 	}

	void run(MessageObject message)
	{
		try{
			//1. creating a socket to connect to the server
			requestSocket = new Socket("localhost", 9000);
			
			
			//2. get Input and Output streams
			out = new DataOutputStream(requestSocket.getOutputStream());
			
			in = new DataInputStream(requestSocket.getInputStream());
			//3: Communicating with the server
			
			byte[] payload = new MessageObjectPacker().pack(message);
			
			int wholeLength = 4 + 16 + 2 + 2 + 2 + 4 
					 + Consts.credential.length() + payload.length;
			
			//Step 1: Write whole length
			out.writeInt(wholeLength);
			
			//Step 1: Write the uuid
			byte[] uuid = asByteArray(UUID.randomUUID());
			
			out.write(uuid, 0, uuid.length);
			
			//Step 1: Write database id
			out.writeShort(Consts.databaseId);
			
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
		new DALClient();
	}
	

}
