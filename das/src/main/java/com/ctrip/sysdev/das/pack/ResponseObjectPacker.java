package com.ctrip.sysdev.das.pack;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import com.ctrip.sysdev.das.enums.ResultType;
import com.ctrip.sysdev.das.msg.AvailableType;
import com.ctrip.sysdev.das.msg.ResponseObject;

public class ResponseObjectPacker {
	
	private static Packer packer;
	
	private static ByteArrayOutputStream out;
	
	static{
		out = new ByteArrayOutputStream();
		MessagePack msgpack = new MessagePack();

		packer = msgpack.createPacker(out);
	}
	
	public static byte[] pack(ResponseObject result) throws Exception{
		
		out.reset();

		packer.writeArrayBegin(result.propertyCount());

		packer.write(result.resultType.getIntVal());
		
		//Write the information of sp
		if(result.resultType == ResultType.RETRIEVE){
			
			packer.write(result.chunkCount);
			packer.write(result.recordPerChunk);
			
		}else{
			
			packer.write(result.affectRowCount);
			
		}

		packer.writeArrayEnd();

		return out.toByteArray();
	}
	
	/**
	 * 
	 * @param chunk
	 * @return
	 * @throws Exception
	 */
	public static byte[] packChunk(List<AvailableType> chunk) 
			throws Exception{
		out.reset();

		packer.writeArrayBegin(chunk.size());
		
		for(int i=0;i<chunk.size();i++){
			packAvailableType(packer, chunk.get(i));
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
			AvailableType availableType) 
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
