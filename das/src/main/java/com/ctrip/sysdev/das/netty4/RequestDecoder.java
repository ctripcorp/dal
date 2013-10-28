package com.ctrip.sysdev.das.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.msgpack.MessagePack;
import org.msgpack.unpacker.Unpacker;

import com.ctrip.sysdev.das.domain.Request;
import com.ctrip.sysdev.das.domain.RequestMessage;
import com.ctrip.sysdev.das.domain.Response;
import com.ctrip.sysdev.das.domain.StatementParameter;
import com.ctrip.sysdev.das.domain.enums.OperationType;
import com.ctrip.sysdev.das.domain.enums.StatementType;
import com.ctrip.sysdev.das.exception.ProtocolInvalidException;
import com.ctrip.sysdev.das.exception.SerDeException;

public class RequestDecoder extends ByteToMessageDecoder {
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		Request request = null; 

		if (in.readableBytes() < 4) {// available byte < packe head
			return;
		}
		in.markReaderIndex();// mark position=0

		int dataLength = in.readInt();// packe size

		if (in.readableBytes() < dataLength) {

			in.resetReaderIndex();// go to mark
			return;
		}
		short protocolVersion = in.readShort();

		byte[] decoded = new byte[dataLength - 2];
		in.readBytes(decoded);
 
		try {
			request = deserialize(decoded);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		if (request != null) {
			out.add(request); 
		}
		Response resp = ctx.attr(Response.RESPONSE_KEY).get();
		resp.decodeEnd();
	}


	private Request deserialize(byte[] source) throws SerDeException {
		Request request = new Request();
		try {
			MessagePack packer = new MessagePack();
			// The object to return
			ByteArrayInputStream in = new ByteArrayInputStream(source);
			Unpacker unpacker = packer.createUnpacker(in);
			int propertyCount = unpacker.readArrayBegin();

			// Property count invalid
			if (propertyCount != currentPropertyCount) {
				throw new ProtocolInvalidException(String.format(
						"Expect property count %d, but got %d instead!",
						currentPropertyCount, propertyCount));
			}
			request.setTaskid(UUID.nameUUIDFromBytes(unpacker.readByteArray()));
			String dbName = unpacker.readString();
			request.setCredential(unpacker.readString());
			request.setMessage(unpackMessage(unpacker));
			request.getMessage().setDbName(dbName);
			unpacker.readArrayEnd();
			unpacker.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SerDeException("RequestObjectSerDe  doDeserialize error",
					e);
		} catch (ProtocolInvalidException e) {
			e.printStackTrace();
			throw new SerDeException("RequestObjectSerDe  doDeserialize error",
					e);
		}
		request.endDecode();
		return request;
	}

	private static final int currentPropertyCount = 4;

	/**
	 * 
	 * @param unpacker
	 * @return
	 * @throws IOException
	 * @throws ProtocolInvalidException
	 */
	private RequestMessage unpackMessage(Unpacker unpacker)
			throws IOException, ProtocolInvalidException {

		int propertyCount = unpacker.readArrayBegin();

		RequestMessage message = new RequestMessage();

		message.setStatementType(StatementType.fromInt(unpacker.readInt()));

		message.setOperationType(OperationType.fromInt(unpacker.readInt()));

		message.setUseCache(unpacker.readBoolean());

		if (message.getStatementType() == StatementType.StoredProcedure) {
			message.setSpName(unpacker.readString());
		} else {
			message.setSql(unpacker.readString());
		}

		int argLength = unpacker.readArrayBegin();

		List<StatementParameter> args = new ArrayList<StatementParameter>(argLength);
		for (int i = 0; i < argLength; i++) {
			args.add(StatementParameter.createFromUnpack(unpacker));
		}
		unpacker.readArrayEnd();
		
		message.setArgs(args);

		message.setFlags(unpacker.readInt());
		
		unpacker.readArrayEnd();

		return message;
	}

}
