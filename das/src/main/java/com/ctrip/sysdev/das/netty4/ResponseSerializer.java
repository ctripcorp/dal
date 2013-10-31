package com.ctrip.sysdev.das.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.type.Value;

import com.ctrip.sysdev.das.domain.Request;
import com.ctrip.sysdev.das.domain.Response;
import com.ctrip.sysdev.das.domain.StatementParameter;
import com.ctrip.sysdev.das.exception.SerDeException;

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
	 * @throws SQLException 
	 */
	public void writeResultSetHeader(ChannelHandlerContext ctx, ResultSet rs) throws Exception {
		ResultSetMetaData metaData = rs.getMetaData();

		int totalColumns = metaData.getColumnCount();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePack msgpack = new MessagePack();
		Packer packer = msgpack.createPacker(out);

		packer.writeArrayBegin(totalColumns);
		for (int i = 0; i < totalColumns; i++) {
			packer.write(metaData.getColumnLabel(i+1));
			packer.write(metaData.getColumnType(i+1));
		}
		packer.writeArrayEnd();
		ctx.writeAndFlush(out.toByteArray());
	}

	/**
	 * For row, we send to sender thread to parallel read and send process.
	 */
	public void write(ChannelHandlerContext ctx, List<Value[]> rows, Response resp) {
		ctx.channel().attr(EXECUTOR_KEY).get().execute(new RowSerializerTask(ctx, rows, resp));
	}
}
