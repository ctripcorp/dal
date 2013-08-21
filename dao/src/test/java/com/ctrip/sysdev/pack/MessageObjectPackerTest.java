package com.ctrip.sysdev.pack;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.sysdev.enums.ActionType;
import com.ctrip.sysdev.enums.Flags;
import com.ctrip.sysdev.enums.MessageType;
import com.ctrip.sysdev.msg.AvailableType;
import com.ctrip.sysdev.msg.MessageObject;

public class MessageObjectPackerTest {

	private static List<AvailableType> myArgs;

	@BeforeClass
	public static void setUp() {
		System.out.println("@Before tearDown");
		myArgs = new ArrayList<AvailableType>();
		for(int i=0;i<1;i++){
			AvailableType arg = new <String> AvailableType(1, "Test");
			myArgs.add(arg);
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

		MessageObject message = new MessageObject();

		message.messageType = MessageType.SQL;
		message.actionType = ActionType.SELECT;
		message.useCache = false;

		message.batchOperation = false;
		message.SQL = "SELECT * FROM Person WHERE Name = ?";
		message.singleArgs = myArgs;

		message.flags = Flags.TEST.getIntVal();
		
		try {
			byte[] payload = MessageObjectPacker.pack(message);
			this.println(String.valueOf(payload.length));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fail("Not yet implemented");
	}

}
