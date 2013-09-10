package com.ctrip.sysdev.das.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.DalService;
import com.ctrip.sysdev.das.domain.Request;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * 
 * @author weiw
 * 
 */
public class Netty4Handler extends SimpleChannelInboundHandler<Request> {

	private static final Logger logger = LoggerFactory
			.getLogger(Netty4Handler.class);

	@Inject
	public Netty4Handler(@Named("ChannelGroup") ChannelGroup allChannels,
			DalService dalServiceImpl) {
		this.allChannels = allChannels;
		this.dalServiceImpl = dalServiceImpl;
	}

	private ChannelGroup allChannels;

	private DalService dalServiceImpl;

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Request request) {
		try {
			logger.info("channelRead0 from {} message = '{}'", ctx.channel(),
					request);

			ByteBuf buf = dalServiceImpl.dalService(request);
			ChannelFuture wf = ctx.channel().writeAndFlush(buf);

			// ChannelFuture wf = channel.writeAndFlush(request);// 回写返回结果
			wf.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future)
						throws Exception {
					if (!future.isSuccess()) {
						logger.error("server write response error ");
					} else {
						logger.info("server write response ok ");
					}
				}
			});
		} catch (Exception e) {
			logger.warn("channelRead0", e);
			ctx.channel().close();
		}
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
