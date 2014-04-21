package com.ctrip.platform.dal.service.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;

import java.util.List;

import com.ctrip.platform.dal.service.domain.DasProto;
import com.ctrip.platform.dal.service.monitors.StatusReportTask;

//TODO revise exception
/**
 * About protocol:
 * 
 * Normal: totalLength, protocol version(>=1), content
 * Heart Beat: totalLength, -1 
 * 
 * @author gawu
 *
 */
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
		
		if(protocolVersion == -1){
			in.discardReadBytes();
			return;
		}

		byte[] decoded = new byte[dataLength - 2];
		in.readBytes(decoded);
 
		DasProto.Request request = null; 
		try {
			request = DasProto.Request.parseFrom(decoded);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			StatusReportTask.getInstance().recordDecodeEnd(request.getId(), decodeStart);
//			long decodeTime = System.currentTimeMillis() - decodeStart;
//			TimeCostSendTask.getInstance().getQueue().add(
//					String.format("id=%s&timeCost=decodeRequestTime:%d", request.getId(), decodeTime));
		}
		
		if (request != null) {
			out.add(request); 
		}
	}

}
