package com.ctrip.sysdev.das.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.msg.MessageObject;

/**
 * 
 * @author weiw
 * 
 */
public class Handler extends SimpleChannelInboundHandler<MessageObject> {

	private static final Logger logger = LoggerFactory.getLogger(Handler.class);
	private ChannelGroup allChannels;

	public Handler(ChannelGroup allChannels) {
		this.allChannels = allChannels;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, MessageObject message) {
		try {
			logger.debug("channelRead0 from {} message = '{}'", ctx.channel(),
					message);
			processIncomingMessage(ctx.channel(), message);
		} catch (Exception e) {
			logger.warn("channelRead0", e);
			ctx.channel().close();
		}
	}

	private void processIncomingMessage(Channel channel,
			final MessageObject message) {
		// ..dboperator.....
		ChannelFuture wf = channel.writeAndFlush(message);// 回写返回结果
		wf.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (!future.isSuccess()) {
					logger.error("server write response error,request = "
							+ message);
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
