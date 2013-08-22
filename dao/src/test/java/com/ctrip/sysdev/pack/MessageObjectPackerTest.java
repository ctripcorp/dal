package com.ctrip.sysdev.pack;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.sysdev.apptools.dao.enums.ActionType;
import com.ctrip.sysdev.apptools.dao.enums.Flags;
import com.ctrip.sysdev.apptools.dao.enums.MessageType;
import com.ctrip.sysdev.apptools.dao.msg.AvailableType;
import com.ctrip.sysdev.apptools.dao.msg.MessageObject;
//import com.ctrip.sysdev.apptools.dao.pack.MessageObjectPacker;

public class MessageObjectPackerTest {

	private static List<AvailableType> myArgs;
	private static MessageObject message;

	@BeforeClass
	public static void setUp() {
		System.out.println("@Before tearDown");
		myArgs = new ArrayList<AvailableType>();
		for(int i=0;i<248;i++){
			AvailableType arg = new <String> AvailableType(1, "Test");
			myArgs.add(arg);
		}
		message = new MessageObject();

		message.messageType = MessageType.SQL;
		message.actionType = ActionType.SELECT;
		message.useCache = false;

		message.batchOperation = false;
		message.SQL = "SELECT * FROM Person WHERE Name = ?";
		message.singleArgs = myArgs;

		message.flags = Flags.TEST.getIntVal();
		
		try {
			//MessageObjectPacker.pack(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void println(String string) {
		System.out.println(string);
	}

	@AfterClass
	public static void tearDown() throws IOException {
		System.out.println("@After tearDown");
		myArgs.clear();
		myArgs = null;
	}
	
	@Test
	public void test() {

		try {
			//byte[] payload = MessageObjectPacker.pack(message);
//			this.println(String.valueOf(payload.length));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fail("Not yet implemented");
	}

}
