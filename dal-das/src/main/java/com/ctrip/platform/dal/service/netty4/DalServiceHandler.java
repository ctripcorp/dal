package com.ctrip.platform.dal.service.netty4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.service.DalServer;
import com.ctrip.platform.dal.service.domain.DasProto;
import com.ctrip.platform.dal.service.monitors.ErrorReporter;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author weiw
 */
public class DalServiceHandler extends SimpleChannelInboundHandler<DasProto.Request> {

	private static final Logger logger = LoggerFactory
			.getLogger(DalServiceHandler.class);

	private ChannelGroup allChannels;
	private QueryExecutor queryExecutor;
	
	@Inject
	public DalServiceHandler(@Named("ChannelGroup") ChannelGroup allChannels) {
		this.allChannels = allChannels;
		queryExecutor = new QueryExecutor(DalServer.DATA_SOURCE);
	}
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, DasProto.Request request) {
		logger.debug("channelRead0 from {} message = '{}'", ctx.channel(), request);
		queryExecutor.execute(request, ctx);
	}
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		logger.debug("channelRegistered {}", ctx.channel());
		allChannels.add(ctx.channel());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.debug("channelActive {}", ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("exceptionCaught {}", ctx.channel(), cause);
		ErrorReporter.reportChannelException(ctx, cause);
		ctx.channel().close();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		logger.debug("channelInactive {}", ctx.channel());
	}
}
