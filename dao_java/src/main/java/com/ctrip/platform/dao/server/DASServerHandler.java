package com.ctrip.platform.dao.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.ByteArrayInputStream;

import org.msgpack.MessagePack;
import org.msgpack.unpacker.Unpacker;

import com.ctrip.platform.dao.param.Parameter;
import com.ctrip.platform.dao.param.ParameterFactory;

public class DASServerHandler extends ChannelInboundHandlerAdapter {

	private ByteBuf wholeBuf;

	private int wholeLength;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Client connected");
		wholeLength = -1;
		wholeBuf = null;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		try {

			int readableLength = buf.readableBytes();

			System.out.println(readableLength);

			ByteBuf all = buf.readBytes(readableLength);

			MessagePack packer = new MessagePack();
			// The object to return
			ByteArrayInputStream in = new ByteArrayInputStream(all.array());
			Unpacker unpacker = packer.createUnpacker(in);

			Parameter param = ParameterFactory.createParameterFromUnpack(unpacker);

			System.out.println(param.getParameterIndex());

			System.out.println(param.getParameterType());

			System.out.println(param.getValue().toString());

		} catch (Exception ex) {
			ex.printStackTrace();
			ctx.close();
		} finally {
			ReferenceCountUtil.release(msg);
			ctx.close();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		// TODO Auto-generated method stub
		cause.printStackTrace();
		ctx.close();
	}

}