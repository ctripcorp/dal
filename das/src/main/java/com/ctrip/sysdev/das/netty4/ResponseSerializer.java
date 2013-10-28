package com.ctrip.sysdev.das.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.domain.Request;
import com.ctrip.sysdev.das.domain.Response;
import com.ctrip.sysdev.das.domain.StatementParameter;
import com.ctrip.sysdev.das.domain.enums.OperationType;
import com.ctrip.sysdev.das.exception.SerDeException;
import com.ctrip.sysdev.das.utils.UUID2ByteArray;

public class ResponseSerializer {
	private static final int currentPropertyCount = 3;
	private static final Logger logger = LoggerFactory
			.getLogger(ResponseSerializer.class);

	private ChannelFutureListener writeCompleteListener = new ChannelFutureListener() {
		public void operationComplete(ChannelFuture future) throws Exception {
			if (!future.isSuccess()) {
				logger.error("server write response error ");
			} else {
				logger.info("server write response ok ");
			}
		}
	};

	public void writeResponseHeader(ChannelHandlerContext ctx, Request request) {
		ByteBuf buffer = ctx.alloc().buffer();
		
		buffer.writeShort(1)
				//.writeBytes(UUID2ByteArray.asByteArray(request.getTaskid()))
				.writeBytes(request.getTaskid().toString().getBytes())
				.writeInt(request.getMessage().getOperationType().getIntVal());
		
		ctx.channel().writeAndFlush(buffer);
	}

	public void writeRowCount(ChannelHandlerContext ctx, Response resp) {
		ByteBuf buffer = ctx.alloc().buffer();
		
		buffer.writeInt(resp.getAffectRowCount());
		ctx.writeAndFlush(buffer).addListener(writeCompleteListener);
		
	}

	public void write(ChannelHandlerContext ctx,
			List<List<StatementParameter>> obj, boolean isLast, Response resp)
			throws SerDeException {
		byte[] bytes = serialize(obj);
		ByteBuf bf = ctx.alloc().buffer();

		bf.writeInt(bytes.length + 1);
		bf.writeByte(isLast? 1 : 0);
		bf.writeBytes(bytes);
		ChannelFuture wf = ctx.writeAndFlush(bf);
		if (!isLast)
			return;

		wf.addListener(writeCompleteListener);
	}

	private byte[] serialize(List<List<StatementParameter>> obj)
			throws SerDeException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePack msgpack = new MessagePack();
		Packer packer = msgpack.createPacker(out);
		try {
			packer.writeArrayBegin(obj.size());
			for (List<StatementParameter> row : obj) {
				packer.writeArrayBegin(row.size());
				for (StatementParameter col : row) {
					col.pack(packer);
				}
				packer.writeArrayEnd();
			}
			packer.writeArrayEnd();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SerDeException("ResponseSerDe doSerialize exception ", e);
		}
		return out.toByteArray();
	}

	/**
	 * @deprecated
	 * @param resp
	 * @return
	 * @throws SerDeException
	 */
	private byte[] serialize(Response resp) throws SerDeException {
		long start = System.currentTimeMillis();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePack msgpack = new MessagePack();
		Packer packer = msgpack.createPacker(out);
		try {
			packer.writeArrayBegin(currentPropertyCount);
			packer.write(UUID2ByteArray.asByteArray(resp.getTaskid()));
			packer.write(resp.getResultType().getIntVal());
			if (resp.getResultType() == OperationType.Read) {
				// means chunk
				// packer.write(obj.getChunkCount());
				packer.writeArrayBegin(resp.getResultSet().size());
				for (List<StatementParameter> outer : resp.getResultSet()) {
					packer.writeArrayBegin(outer.size());
					for (StatementParameter inner : outer) {
						inner.pack(packer);
					}
					packer.writeArrayEnd();
				}
				packer.writeArrayEnd();
			} else {
				packer.write(resp.getAffectRowCount());
			}
			packer.write(resp.getTotalTime());
			packer.write(resp.getDecodeRequestTime());
			packer.write(resp.getDbTime());
			packer.write(resp.getEncodeResponseTime());
			packer.writeArrayEnd();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new SerDeException("ResponseSerDe doSerialize exception ", e);
		}

		byte[] bytes = out.toByteArray();
		return bytes;
	}
}
