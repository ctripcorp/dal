package com.ctrip.sysdev.pack;

//import com.ctrip.sysdev.apptools.dao.pack.MessageObjectPacker;

public class MessageObjectPackerPerformance {

//	public void evaluate(int count) {
//
//		List<AvailableType> myArgs;
//		myArgs = new ArrayList<AvailableType>();
//
//		for (int i = 0; i < count; i++) {
//			AvailableType arg = new<String> AvailableType(1, "Test");
//			myArgs.add(arg);
//		}
//
//		MessageObject message = new MessageObject();
//
//		message.messageType = MessageType.SQL;
//		message.actionType = ActionType.SELECT;
//		message.useCache = false;
//
//		message.batchOperation = false;
//		message.SQL = "SELECT * FROM Person WHERE Name = ?";
//		message.singleArgs = myArgs;
//
//		message.flags = Flags.TEST.getIntVal();
//
//		try {
//			byte[] payload = null;
//			//payload = MessageObjectPacker.pack(message);
//			long startTime = System.nanoTime();
//
//			payload = MessageObjectPacker.pack(message);
//
//			System.out.println("**=" + startTime);
//
//			long endTime = System.nanoTime();
//
//			System.out.println((endTime - startTime) / 1000000.0);
//			System.out.println(String.valueOf(payload.length));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	public static void main(String[] args) {
//		new MessageObjectPackerPerformance().evaluate(10);
	}

}
