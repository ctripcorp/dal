package com.ctrip.sysdev.das.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.ctrip.sysdev.das.domain.Request;
import com.ctrip.sysdev.das.domain.Response;
import com.ctrip.sysdev.das.domain.StatementParameter;

public class ResponseSerializer {
	public static final AttributeKey<Executor> EXECUTOR_KEY = new AttributeKey<Executor>("EXECUTOR_KEY");

	public static void initChannel(ChannelHandlerContext ctx) {
		ctx.channel().attr(EXECUTOR_KEY).set(Executors.newSingleThreadExecutor());
	}
	
	/**
	 * For header, we send directly to netty.
	 */
	public void writeResponseHeader(ChannelHandlerContext ctx, Request request) {
		ByteBuf buffer = ctx.alloc().buffer();
		
		buffer.writeShort(1)
				.writeBytes(request.getTaskid().toString().getBytes())
				.writeInt(request.getMessage().getOperationType().getIntVal());
		
		ctx.channel().writeAndFlush(buffer);
	}

	/**
	 * For count, we send directly to netty.
	 */
	public void writeRowCount(ChannelHandlerContext ctx, Response resp) {
		ByteBuf buffer = ctx.alloc().buffer();
		
		buffer.writeInt(resp.getAffectRowCount());
		ctx.writeAndFlush(buffer).addListener(new ResponseWriteCompleteListener(resp));
	}

	/**
	 * For row, we send to sender thread to parallel read and send process.
	 */
	public void write(ChannelHandlerContext ctx, List<List<StatementParameter>> obj, Response resp) {
		ctx.channel().attr(EXECUTOR_KEY).get().execute(new RowSerializerTask(ctx, obj, resp));
	}
}
