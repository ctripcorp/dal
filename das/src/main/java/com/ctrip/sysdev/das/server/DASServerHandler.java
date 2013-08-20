package com.ctrip.sysdev.das.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.UUID;

import com.ctrip.sysdev.das.msg.MessageObject;
import com.ctrip.sysdev.das.pack.MessageObjectUnPacker;

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

			if (wholeLength < 0 && readableLength < 4) {
				throw new Exception("Packet invalid!");
			}

			if (wholeLength < 0) {
				wholeLength = buf.readInt();
				wholeBuf = Unpooled.buffer(wholeLength - 4);
				// wholeBuf.writeInt(wholeLength);
			}

			if (buf.readableBytes() > 0) {
				wholeBuf.writeBytes(buf);
			}

//			buf.release();

//			System.out.println("readable: " + wholeBuf.readableBytes());

			if (wholeBuf.readableBytes() == wholeBuf.capacity()) {

				// Read the whole length of the packet
				// int whole_len = buf.readInt();
				// System.out.println("total length: " + whole_len);

				// Read the uuid of current task
				// ByteBuf uuid = wholeBuf.readBytes(36);
				ByteBuf uuid = wholeBuf.readBytes(16);

				// System.out.println("task id: "
				// + uuid.toString(io.netty.util.CharsetUtil.US_ASCII));
				System.out.println(UUID.nameUUIDFromBytes(uuid.array()));

				// Read the database id
				short db_id = wholeBuf.readShort();

				System.out.println("database id: " + db_id);

				// read the length of credential
				short cred_len = wholeBuf.readShort();

				System.out.println("credential length: " + cred_len);

				// read the credential
				ByteBuf cred = wholeBuf.readBytes(cred_len);

				System.out.println("credential: "
						+ cred.toString(io.netty.util.CharsetUtil.US_ASCII));

				short payload_ver = wholeBuf.readShort();

				System.out.println("payload version: " + payload_ver);

				// read the length of payload
				int payload_len = wholeBuf.readInt();

				System.out.println("payload length: " + payload_len);

				// read the payload
				ByteBuf payload = wholeBuf.readBytes(payload_len);

				byte[] payload_array = payload.array();

				MessageObject myMessage = new MessageObjectUnPacker()
						.unpack(payload_array);
				
				

				System.out
						.println("A single request object read finish--------------------------------------");

				uuid.release();
				cred.release();
				payload.release();

				wholeBuf.release();

				ctx.close();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			ctx.close();
		} finally {
			ReferenceCountUtil.release(msg);
			// ctx.close();
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
