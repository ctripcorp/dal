package com.ctrip.sysdev.pack;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import com.ctrip.sysdev.enums.MessageType;
import com.ctrip.sysdev.msg.AvailableType;
import com.ctrip.sysdev.msg.MessageObject;

public class MessageObjectPacker {
	
	private static Packer packer;
	
	private static ByteArrayOutputStream out;
	
	static{
		out = new ByteArrayOutputStream();

		MessagePack msgpack = new MessagePack();

		packer = msgpack.createPacker(out);
	}

	public static byte[] pack(MessageObject msg) throws Exception {

		out.reset();

		packer.writeArrayBegin(msg.propertyCount());

		packer.write(msg.messageType.getIntVal());

		packer.write(msg.actionType.getIntVal());

		packer.write(msg.useCache);
		
		//Write the information of sp
		if(msg.messageType == MessageType.SP){
			
			packer.write(msg.SPName);
			
			packer.writeArrayBegin(msg.singleArgs.size());
			
			for(AvailableType at: msg.singleArgs){
				packAvailableType(packer, at);
			}
			
			packer.writeArrayEnd();

			// END stored procedure params pack ----------------------
		}else{
			
			packer.write(msg.batchOperation);
			
			packer.write(msg.SQL);
			
			if(msg.batchOperation){
				
				packer.writeArrayBegin(msg.batchArgs.size());
				
				for(List<AvailableType> arg : msg.batchArgs){
					
					packer.writeArrayBegin(arg.size());
					
					for(AvailableType at: arg){
						packAvailableType(packer, at);
					}
					packer.writeArrayEnd();
					
				}
				
				packer.writeArrayEnd();
				
			}else{
				
				packer.writeArrayBegin(msg.singleArgs.size());
				
				for(AvailableType at: msg.singleArgs){
					packAvailableType(packer, at);
				}
				
				packer.writeArrayEnd();
				
			}
			
		}

		packer.writeArrayEnd();

		return out.toByteArray();
	}
	
	private static void packAvailableType(Packer packer, AvailableType availableType) 
			throws Exception{
		
		packer.writeArrayBegin(3);
		packer.write(availableType.paramIndex);
		packer.write(availableType.currentType.getIntVal());
		switch(availableType.currentType){
		case BOOL:
			packer.write(availableType.bool_arg);
			break;
		case BYTE:
			packer.write(availableType.byte_arg);
			break;
		case SHORT:
			packer.write(availableType.short_arg);
			break;
		case INT:
			packer.write(availableType.int_arg);
			break;
		case LONG:
			packer.write(availableType.long_arg);
			break;
		case FLOAT:
			packer.write(availableType.float_arg);
			break;
		case DOUBLE:
			packer.write(availableType.double_arg);
			break;
		case DECIMAL:
			packer.write(availableType.decimal_arg);
			break;
		case STRING:
			packer.write(availableType.string_arg);
			break;
		case DATETIME:
			packer.write(availableType.datetime_arg);
			break;
		case BYTEARR:
			packer.write(availableType.bytearr_arg);
			break;
		default:
			packer.write(availableType.object_arg);
			break;
		}
		packer.writeArrayEnd();
		
	}

}
