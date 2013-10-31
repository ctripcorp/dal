package com.ctrip.sysdev.das.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.type.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.domain.Response;

public class RowSerializerTask implements Runnable {
	private static final Logger logger = LoggerFactory
			.getLogger(RowSerializerTask.class);
	private ChannelHandlerContext ctx;
	private List<Value[]> rows;
	private Response response;
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	MessagePack msgpack = new MessagePack();
	Packer packer = msgpack.createPacker(out);
	
	public RowSerializerTask(ChannelHandlerContext ctx, List<Value[]> rows, Response response) {
		this.ctx = ctx;
		this.rows = rows;
		this.response = response;
	}
	
	@Override
	public void run() {
		try {
			byte[] bytes = serialize(rows);
			ByteBuf bf = ctx.alloc().buffer();

			bf.writeInt(bytes.length + 1);
			bf.writeByte(response == null? 0 : 1);
			bf.writeBytes(bytes);
			ChannelFuture wf = ctx.writeAndFlush(bf);
			
			// Only the last one need to pass response
			if (response == null)
				return;

			wf.addListener(new ResponseWriteCompleteListener(response));
		} catch (Exception e) {
			logger.error("Error", e);
		}
		rows.clear();
		rows = null;

	}
	

	private byte[] serialize(List<Value[]> rows) throws Exception {
		out.reset();
		packer.writeArrayBegin(rows.size());
		for (Value[] row : rows) {
			packer.writeArrayBegin(row.length);
			for(Value column: row) {
				packer.write(column);
			}
			packer.writeArrayEnd();
		}
		packer.writeArrayEnd();
		return out.toByteArray();
	}

}
