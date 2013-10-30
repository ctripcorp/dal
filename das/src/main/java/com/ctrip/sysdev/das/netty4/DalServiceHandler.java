package com.ctrip.sysdev.das.netty4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.DruidDataSourceWrapper;
import com.ctrip.sysdev.das.domain.Request;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author weiw
 */
public class DalServiceHandler extends SimpleChannelInboundHandler<Request> {

	private static final Logger logger = LoggerFactory
			.getLogger(DalServiceHandler.class);

	private ChannelGroup allChannels;
	private QueryExecutor queryExecutor;
	
	@Inject
	public DalServiceHandler(@Named("ChannelGroup") ChannelGroup allChannels) {
		this.allChannels = allChannels;
		queryExecutor = new QueryExecutor(DruidDataSourceWrapper.dataSource);
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Request request) {
		try {
			logger.info("channelRead0 from {} message = '{}'", ctx.channel(), request);
			queryExecutor.execute(request, ctx);
		} catch (Throwable e) {
			logger.warn("channelRead0", e);
			ctx.channel().close();
		}
	}
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		logger.debug("channelRegistered {}", ctx.channel());
		allChannels.add(ctx.channel());
		ResponseSerializer.initChannel(ctx);
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
