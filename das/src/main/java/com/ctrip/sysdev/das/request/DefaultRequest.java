package com.ctrip.sysdev.das.request;

import static org.msgpack.template.Templates.TBigDecimal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.msgpack.MessagePack;
import org.msgpack.unpacker.Unpacker;

import com.ctrip.sysdev.das.enums.ActionTypeEnum;
import com.ctrip.sysdev.das.enums.AvailableTypeEnum;
import com.ctrip.sysdev.das.enums.MessageTypeEnum;
import com.ctrip.sysdev.das.exception.ProtocolInvalidException;
import com.ctrip.sysdev.das.msg.AvailableType;
import com.ctrip.sysdev.das.msg.Message;

/**
 * If not specified, das will use this request to parse the protocol
 * 
 * @author gawu
 * 
 */
public class DefaultRequest extends AbstractRequest {

	private static final DefaultRequest defaultRequest = new DefaultRequest();

	private static final int currentPropertyCount = 4;

	/**
	 * Initialize the protocol version to 1
	 */
	private DefaultRequest() {
		protocolVersion = 1;
	}

	/**
	 * Get the default instance, mainly used to get the protocol version
	 * 
	 * @return
	 */
	public static DefaultRequest getDefaultInstance() {
		return defaultRequest;
	}

	private UUID taskid;

	private String dbName;

	private String credential;

	private Message message;

	public UUID getTaskid() {
		return taskid;
	}

	public void setTaskid(UUID taskid) {
		this.taskid = taskid;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public static DefaultRequest unpackFromBytes(byte[] payload)
			throws IOException, ProtocolInvalidException {

		MessagePack packer = new MessagePack();

		// The object to return
		DefaultRequest request = new DefaultRequest();

		ByteArrayInputStream in = new ByteArrayInputStream(payload);
		Unpacker unpacker = packer.createUnpacker(in);

		int propertyCount = unpacker.readArrayBegin();

		// Property count invalid
		if (propertyCount != currentPropertyCount) {
			throw new ProtocolInvalidException(String.format(
					"Expect property count %d, but got %d instead!",
					currentPropertyCount, propertyCount));
		}

		request.taskid = UUID.nameUUIDFromBytes(unpacker.readByteArray());

		request.dbName = unpacker.readString();

		request.credential = unpacker.readString();

		request.message = unpackMessage(unpacker);

		unpacker.readArrayEnd();

		unpacker.close();

		return request;

	}
	
	/**
	 * 
	 * @param unpacker
	 * @return
	 * @throws IOException
	 * @throws ProtocolInvalidException
	 */
	private static Message unpackMessage(Unpacker unpacker) throws IOException,
			ProtocolInvalidException {

		int propertyCount = unpacker.readArrayBegin();

		Message message = new Message();

		message.setMessageType(MessageTypeEnum.fromInt(unpacker.readInt()));

		message.setActionType(ActionTypeEnum.fromInt(unpacker.readInt()));

		message.setUseCache(unpacker.readBoolean());

		if (message.getMessageType() == MessageTypeEnum.SP) {
			message.setSpName(unpacker.readString());
		} else {
			message.setSql(unpacker.readString());
		}

		int argsLength = unpacker.readArrayBegin();
		message.setArgs(new ArrayList<List<AvailableType>>(argsLength));

		for (int i = 0; i < argsLength; i++) {

			int argLength = unpacker.readArrayBegin();
			List<AvailableType> arg = new ArrayList<AvailableType>(argLength);
			for (int j = 0; j < argLength; j++) {
				arg.add(unpackAvailableType(unpacker));
			}
			unpacker.readArrayEnd();
			message.getArgs().add(arg);
		}

		unpacker.readArrayEnd();

		message.setFlags(unpacker.readInt());

		return message;
	}
	
	/**
	 * 
	 * @param unpacker
	 * @return
	 * @throws IOException
	 */
	private static AvailableType unpackAvailableType(Unpacker unpacker)
			throws IOException {

		AvailableType at = new AvailableType();

		int propertyCount = unpacker.readArrayBegin();

		at.currentType = AvailableTypeEnum.fromInt(unpacker.readInt());

		switch (at.currentType) {
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
