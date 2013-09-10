package {{product_line}}.{{domain}}.{{app_name}}.dao.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import {{product_line}}.{{domain}}.{{app_name}}.dao.enums.MessageTypeEnum;
import {{product_line}}.{{domain}}.{{app_name}}.dao.exception.ProtocolInvalidException;
import {{product_line}}.{{domain}}.{{app_name}}.dao.param.Parameter;
import {{product_line}}.{{domain}}.{{app_name}}.dao.utils.UUID2ByteArray;

/**
 * If not specified, das will use this request to parse the protocol
 * 
 * @author gawu
 * 
 */
public class DefaultRequest extends AbstractRequest {

	public RequestMessage getMessage() {
		return message;
	}

	public void setMessage(RequestMessage message) {
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

	private RequestMessage message;

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
	private void packMessage(Packer packer, RequestMessage message)
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

		for (List<Parameter> row : message.getArgs()) {

			packer.writeArrayBegin(row.size());

			for (Parameter col : row) {
				col.pack(packer);
			}

			packer.writeArrayEnd();
		}

		packer.writeArrayEnd();

		packer.write(message.getFlags());

		packer.writeArrayEnd();
	}

	
}
