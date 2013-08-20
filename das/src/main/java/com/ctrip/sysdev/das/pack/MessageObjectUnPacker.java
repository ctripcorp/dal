package com.ctrip.sysdev.das.pack;

import static org.msgpack.template.Templates.TBigDecimal;
import static org.msgpack.template.Templates.TBoolean;
import static org.msgpack.template.Templates.TInteger;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.msgpack.MessagePack;
import org.msgpack.unpacker.Unpacker;

import com.ctrip.sysdev.das.enums.ActionType;
import com.ctrip.sysdev.das.enums.AvailableTypeEnum;
import com.ctrip.sysdev.das.enums.MessageType;
import com.ctrip.sysdev.das.msg.AvailableType;
import com.ctrip.sysdev.das.msg.MessageObject;

public class MessageObjectUnPacker {
	
	public MessageObject unpack(byte[] payload) throws Exception {

		MessagePack packer = new MessagePack();
	    
	    //The object to return
	    MessageObject myMessage = new MessageObject();
	    
	    ByteArrayInputStream in = new ByteArrayInputStream(payload);
	    Unpacker unpacker = packer.createUnpacker(in);
	    
	    //The count of the object properties
	    int propertyCount = unpacker.readArrayBegin();

	    //The first if an integer, 0 represents SQL, 1 represents SP
	    myMessage.messageType = MessageType.fromInt(unpacker.read(TInteger));
	    
	    //Only available when messageType is 0
	    //SELECt 0, CREATE 1, UPDATE 2, DELETE 3
	    myMessage.actionType = ActionType.fromInt(unpacker.read(TInteger));
	    
	    //Use cache as first consideration or not
	    myMessage.useCache = unpacker.read(TBoolean);
		
		//Write the information of sp
		if(myMessage.messageType == MessageType.SP){
			
			myMessage.SPName = unpacker.readString();
			
			List<AvailableType> singleArgs = new LinkedList<AvailableType>();
			int argsSize = unpacker.readArrayBegin();
			for(int j=0;j<argsSize;j++){
				singleArgs.add(unpackAvailableType(unpacker));
			}
			unpacker.readArrayEnd();
			
			myMessage.singleArgs = singleArgs;
			
		}else{
			myMessage.batchOperation = unpacker.readBoolean();
			
			myMessage.SQL = unpacker.readString();
			
			if(myMessage.batchOperation){
				
				int batchSize = unpacker.readArrayBegin();
				
				List<List<AvailableType>> batchArgs = new LinkedList<List<AvailableType>>();
				
				for(int i=0;i<batchSize;i++){
					List<AvailableType> batchArg = new LinkedList<AvailableType>();
					int argsSize = unpacker.readArrayBegin();
					for(int j=0;j<argsSize;j++){
						batchArg.add(unpackAvailableType(unpacker));
					}
					unpacker.readArrayEnd();
					batchArgs.add(batchArg);
				}
				
				unpacker.readArrayEnd();
				
				myMessage.batchArgs = batchArgs;
				
			}else{
				
				List<AvailableType> singleArgs = new LinkedList<AvailableType>();
				int argsSize = unpacker.readArrayBegin();
				for(int j=0;j<argsSize;j++){
					singleArgs.add(unpackAvailableType(unpacker));
				}
				unpacker.readArrayEnd();
				
				myMessage.singleArgs = singleArgs;
			}
			
		}

		unpacker.readArrayEnd();

		return myMessage;
	}
	
	/**
	 * Convert AvailableType to MsgPack format
	 * 
	 * @param packer
	 * @param availableType
	 * @throws Exception
	 */
	private AvailableType unpackAvailableType(Unpacker unpacker) 
			throws Exception{
		
		AvailableType at = new AvailableType();
		
		int propertyCount = unpacker.readArrayBegin();
		
		at.currentType = AvailableTypeEnum.fromInt(unpacker.readInt());

		switch(at.currentType){
		case BOOL:
			at.bool_arg = unpacker.readBoolean();
			break;
		case BYTE:
			at.byte_arg = unpacker.readByte();
			break;
		case SHORT:
			at.short_arg = unpacker.readShort();
			break;
		case INT:
			at.int_arg = unpacker.readInt();
			break;
		case LONG:
			at.long_arg = unpacker.readLong();
			break;
		case FLOAT:
			at.float_arg = unpacker.readFloat();
			break;
		case DOUBLE:
			at.double_arg = unpacker.readDouble();
			break;
		case DECIMAL:
			at.decimal_arg = unpacker.read(TBigDecimal);
			break;
		case STRING:
			at.string_arg = unpacker.readString();
			break;
		case DATETIME:
			at.datetime_arg = unpacker.read(Timestamp.class);
			break;
		case BYTEARR:
			at.bytearr_arg = unpacker.readByteArray();
			break;
		default:
			at.object_arg = unpacker.readValue();
		}
		unpacker.readArrayEnd();
		
		return at;
	}

}
