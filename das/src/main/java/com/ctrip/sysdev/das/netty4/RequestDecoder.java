package com.ctrip.sysdev.das.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.msgpack.MessagePack;
import org.msgpack.unpacker.Unpacker;

import com.ctrip.sysdev.das.domain.DasProto;
import com.ctrip.sysdev.das.exception.ProtocolInvalidException;
import com.ctrip.sysdev.das.exception.SerDeException;

//TODO revise exception
public class RequestDecoder extends ByteToMessageDecoder {
	
	static final AttributeKey<Long> DECODE_START = new AttributeKey<Long>("DECODE_START");
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		
		//TODO: Add decode start
		long decodeStart = System.currentTimeMillis();

		if (in.readableBytes() < 4) {// available byte < packe head
			return;
		}
		in.markReaderIndex();// mark position=0

		int dataLength = in.readInt();// packe size

		if (in.readableBytes() < dataLength) {

			in.resetReaderIndex();// go to mark
			return;
		}
		
		//Only use when the protocol change, now unused
		short protocolVersion = in.readShort();

		byte[] decoded = new byte[dataLength - 2];
		in.readBytes(decoded);
 
		DasProto.Request request = null; 
		try {
			request = DasProto.Request.parseFrom(decoded);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			long decodeTime = System.currentTimeMillis() - decodeStart;
			TimeCostSendTask.getInstance().getQueue().add(
					String.format("id=%s&timeCost=decodeRequestTime:%d", request.getId(), decodeTime));
		}
		
		if (request != null) {
			out.add(request); 
		}
	}

}
