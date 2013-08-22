package com.ctrip.sysdev.das.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.enums.ResultType;
import com.ctrip.sysdev.das.msg.RequestObject;

/**
 * 
 * @author weiw
 * 
 */
public class Handler extends SimpleChannelInboundHandler<RequestObject> {

	private static final Logger logger = LoggerFactory.getLogger(Handler.class);
	private ChannelGroup allChannels;

	public Handler(ChannelGroup allChannels) {
		this.allChannels = allChannels;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, RequestObject request) {
		try {
			logger.debug("channelRead0 from {} message = '{}'", ctx.channel(),
					request);
			processIncomingMessage(ctx.channel(), request);
		} catch (Exception e) {
			logger.warn("channelRead0", e);
			ctx.channel().close();
		}
	}

	private void processIncomingMessage(Channel channel,
			final RequestObject request) {
		// ..dboperator.....
		
		ByteBuf buf = Unpooled.buffer(10);
		
		buf.writeShort(1);
		buf.writeInt(ResultType.CUD.getIntVal());
		buf.writeInt(1);
		
//		channel.write(1);
//		channel.write(ResultType.CUD);
		ChannelFuture wf =  channel.writeAndFlush(buf);
		
		//ChannelFuture wf = channel.writeAndFlush(request);// 回写返回结果
		wf.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (!future.isSuccess()) {
					logger.error("server write response error,request = "
							+ request);
				}
			}
		});
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		logger.debug("channelRegistered {}", ctx.channel());
		allChannels.add(ctx.channel());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("channelActive {}", ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.debug("exceptionCaught {}", ctx.channel(), cause);
		ctx.channel().close();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		logger.info("channelInactive {}", ctx.channel());
	}
}
