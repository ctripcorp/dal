package com.ctrip.sysdev.das.pack;

import static org.msgpack.template.Templates.TBigDecimal;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.msgpack.MessagePack;
import org.msgpack.unpacker.Unpacker;

import com.ctrip.sysdev.das.enums.ActionType;
import com.ctrip.sysdev.das.enums.AvailableTypeEnum;
import com.ctrip.sysdev.das.enums.MessageType;
import com.ctrip.sysdev.das.msg.AvailableType;
import com.ctrip.sysdev.das.msg.MessageObject;
import com.ctrip.sysdev.das.msg.RequestObject;

public class RequestObjectUnPacker {

	public static RequestObject unpack(byte[] payload) throws Exception {

		MessagePack packer = new MessagePack();

		// The object to return
		RequestObject request = new RequestObject();

		ByteArrayInputStream in = new ByteArrayInputStream(payload);
		Unpacker unpacker = packer.createUnpacker(in);

		int propertyCount = unpacker.readArrayBegin();
		
		request.taskid = UUID.nameUUIDFromBytes(unpacker.readByteArray());
		
		request.dbName = unpacker.readString();
		
		request.credential = unpacker.readString();
		
		request.message = unpackMessage(unpacker);
		
		unpacker.readArrayEnd();

		return request;
	}

	private static MessageObject unpackMessage(Unpacker unpacker)
			throws Exception {

		int propertyCount = unpacker.readArrayBegin();
		
		MessageObject message = new MessageObject();
		
		message.messageType = MessageType.fromInt(unpacker.readInt());
		
		message.actionType = ActionType.fromInt(unpacker.readInt());
		
		message.useCache = unpacker.readBoolean();

		// Write the information of sp
		if (message.messageType == MessageType.SP) {

			message.SPName = unpacker.readString();

			int argsLength = unpacker.readArrayBegin();
			
			message.singleArgs = new ArrayList<AvailableType>(argsLength);
			for(int i=0;i<argsLength;i++){
				message.singleArgs.add(unpackAvailableType(unpacker));
			}

			unpacker.readArrayEnd();

			// END stored procedure params pack ----------------------
		} else {
			
			message.batchOperation = unpacker.readBoolean();
			
			message.SQL = unpacker.readString();

			if (message.batchOperation) {

				int batchArgsLength = unpacker.readArrayBegin();
				message.batchArgs = new ArrayList<List<AvailableType>>(batchArgsLength);

				for (int i=0;i<batchArgsLength;i++) {
					
					int argsLength = unpacker.readArrayBegin();
					List<AvailableType> arg = new ArrayList<AvailableType>(argsLength);
					for (int j=0;j<argsLength;j++) {
						arg.add(unpackAvailableType(unpacker));
					}
					unpacker.readArrayEnd();
					message.batchArgs.add(arg);
				}

				unpacker.readArrayEnd();

			} else {

				int argsLength = unpacker.readArrayBegin();
				
				message.singleArgs = new ArrayList<AvailableType>(argsLength);

				for (int j=0;j<argsLength;j++) {
					message.singleArgs.add(unpackAvailableType(unpacker));
				}
				
				unpacker.readArrayEnd();

			}

		}
		
		message.flags = unpacker.readInt();

		return message;
	}

	/**
	 * Convert AvailableType to MsgPack format
	 * 
	 * @param packer
	 * @param availableType
	 * @throws Exception
	 */
	private static AvailableType unpackAvailableType(Unpacker unpacker) 
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
