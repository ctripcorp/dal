package com.ctrip.sysdev.das.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import com.ctrip.sysdev.das.enums.ResultTypeEnum;
import com.ctrip.sysdev.das.msg.AvailableType;
import com.ctrip.sysdev.das.utils.UUID2ByteArray;

public class DefaultResponse extends AbstractResponse {

	private static Packer packer;

	private static ByteArrayOutputStream out;

	private static final int currentPropertyCount = 3;

	static {
		out = new ByteArrayOutputStream();
		MessagePack msgpack = new MessagePack();

		packer = msgpack.createPacker(out);
	}

	private UUID taskid;
	
	private ResultTypeEnum resultType;

	private int affectRowCount;
	
	private List<List<AvailableType>> resultSet;

	public List<List<AvailableType>> getResultSet() {
		return resultSet;
	}

	public void setResultSet(List<List<AvailableType>> resultSet) {
		this.resultSet = resultSet;
	}

	public UUID getTaskid() {
		return taskid;
	}

	public void setTaskid(UUID taskid) {
		this.taskid = taskid;
	}

	public ResultTypeEnum getResultType() {
		return resultType;
	}

	public void setResultType(ResultTypeEnum resultType) {
		this.resultType = resultType;
	}

	public int getAffectRowCount() {
		return affectRowCount;
	}

	public void setAffectRowCount(int affectRowCount) {
		this.affectRowCount = affectRowCount;
	}

	/**
	 * Pack the response object for socket write
	 * 
	 * @return
	 * @throws Exception
	 */
	public byte[] pack() throws IOException {

		out.reset();

		packer.writeArrayBegin(currentPropertyCount);

		packer.write(UUID2ByteArray.asByteArray(taskid));

		packer.write(resultType.getIntVal());

		if (resultType == ResultTypeEnum.RETRIEVE) {
			// means chunk
			packer.write(1);

		} else {

			packer.write(affectRowCount);

		}

		packer.writeArrayEnd();

		return out.toByteArray();
	}
	
	/**
	 * Pack a chunk of data, some records of data
	 * @param chunk
	 * @return
	 * @throws IOException
	 */
	public static byte[] packChunk(List<List<AvailableType>> chunk)
			throws IOException {
		
		out.reset();
		
		packer.writeArrayBegin(chunk.size());
		
		for(List<AvailableType> row : chunk){
			
			packer.writeArrayBegin(row.size());
			
			for(AvailableType col : row){
				packAvailableType(packer, col);
			}
			
			packer.writeArrayEnd();
		}
		
		packer.writeArrayEnd();
		
		return out.toByteArray();
		
	}

	/**
	 * 
	 * @param packer
	 * @param availableType
	 * @throws Exception
	 */
	private static void packAvailableType(Packer packer,
			AvailableType availableType) throws IOException {

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
