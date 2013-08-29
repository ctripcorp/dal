package com.ctrip.platform.dao.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import com.ctrip.platform.dao.enums.MessageTypeEnum;
import com.ctrip.platform.dao.exception.ProtocolInvalidException;
import com.ctrip.platform.dao.msg.AvailableType;
import com.ctrip.platform.dao.msg.Message;
import com.ctrip.platform.dao.utils.UUID2ByteArray;

/**
 * If not specified, das will use this request to parse the protocol
 * 
 * @author gawu
 * 
 */
public class DefaultRequest extends AbstractRequest {

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	private static final DefaultRequest defaultRequest = new DefaultRequest();

	private static final int currentPropertyCount = 4;

	private static Packer packer;

	private static ByteArrayOutputStream out;

	static {
		out = new ByteArrayOutputStream();
		MessagePack msgpack = new MessagePack();

		packer = msgpack.createPacker(out);
	}

	/**
	 * Initialize the protocol version to 1
	 */
	public DefaultRequest() {
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

	public byte[] packToByteArray() throws IOException,
			ProtocolInvalidException {

		out.reset();

		packer.writeArrayBegin(currentPropertyCount);

		packer.write(UUID2ByteArray.asByteArray(taskid));

		packer.write(dbName);

		packer.write(credential);

		packMessage(packer, message);

		packer.writeArrayEnd();

		return out.toByteArray();

	}

	/**
	 * 
	 * @param unpacker
	 * @return
	 * @throws IOException
	 * @throws ProtocolInvalidException
	 */
	private void packMessage(Packer packer, Message message)
			throws IOException, ProtocolInvalidException {

		packer.writeArrayBegin(message.propertyCount());

		packer.write(message.getMessageType().getIntVal());

		packer.write(message.getActionType().getIntVal());

		packer.write(message.isUseCache());

		if (message.getMessageType() == MessageTypeEnum.SP) {
			packer.write(message.getSpName());
		} else {
			packer.write(message.getSql());
		}

		packer.writeArrayBegin(message.getArgs().size());

		for (List<AvailableType> row : message.getArgs()) {

			packer.writeArrayBegin(row.size());

			for (AvailableType col : row) {
				packAvailableType(packer, col);
			}

			packer.writeArrayEnd();
		}

		packer.writeArrayEnd();

		packer.write(message.getFlags());

		packer.writeArrayEnd();
	}

	/**
	 * 
	 * @param packer
	 * @param availableType
	 * @throws Exception
	 */
	private void packAvailableType(Packer packer, AvailableType availableType)
			throws IOException {

		packer.writeArrayBegin(3);
		packer.write(availableType.paramIndex);
		packer.write(availableType.currentType.getIntVal());
		switch (availableType.currentType) {
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
