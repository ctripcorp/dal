package com.ctrip.sysdev.pack;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.sysdev.enums.ActionType;
import com.ctrip.sysdev.enums.Flags;
import com.ctrip.sysdev.enums.MessageType;
import com.ctrip.sysdev.msg.AvailableType;
import com.ctrip.sysdev.msg.MessageObject;

public class MessageObjectPackerPerformance {

	public void evaluate(int count) {
		
		long startTime = System.nanoTime();
		List<AvailableType> myArgs;
		myArgs = new ArrayList<AvailableType>();

		for (int i = 0; i < count; i++) {
			AvailableType arg = new<String> AvailableType(1, "Test");
			myArgs.add(arg);
		}
		long endTime = System.nanoTime();
		
		long duration = endTime - startTime;
		
		System.out.println(duration / 1000000.0);
		
		startTime = System.nanoTime();
		
		MessageObject message = new MessageObject();

		message.messageType = MessageType.SQL;
		message.actionType = ActionType.SELECT;
		message.useCache = false;

		message.batchOperation = false;
		message.SQL = "SELECT * FROM Person WHERE Name = ?";
		message.singleArgs = myArgs;

		message.flags = Flags.TEST.getIntVal();
		
		try {
			startTime = System.nanoTime();
			byte[] payload = MessageObjectPacker.pack(message);
			endTime = System.nanoTime();
			System.out.println((endTime - startTime) / 1000000.0);
			System.out.println(String.valueOf(payload.length));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		new MessageObjectPackerPerformance().evaluate(100000);
	}

}
