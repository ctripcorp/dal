package com.ctrip.sysdev.das.netty4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.DruidDataSourceWrapper;
import com.ctrip.sysdev.das.domain.Request;
import com.ctrip.sysdev.das.domain.Response;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * 
 * @author weiw
 * 
 */
public class DalServiceHandler extends SimpleChannelInboundHandler<Request> {

	private static final Logger logger = LoggerFactory
			.getLogger(DalServiceHandler.class);

	@Inject
	public DalServiceHandler(@Named("ChannelGroup") ChannelGroup allChannels, ResponseSerializer msgPackSerDe) {
		this.allChannels = allChannels;
		this.msgPackSerDe = msgPackSerDe;
		dataSourceWrapper = DruidDataSourceWrapper.dataSource;
	}

	private ChannelGroup allChannels;
	private ResponseSerializer msgPackSerDe;
	private Executor timeCostSender = Executors.newSingleThreadExecutor();
	private DruidDataSourceWrapper dataSourceWrapper;
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, Request request) {
		try {
			logger.info("channelRead0 from {} message = '{}'", ctx.channel(), request);

			msgPackSerDe.writeResponseHeader(ctx, request);
			Response response = new QueryExecutor(dataSourceWrapper,request.getMessage(), ctx).execute();
			response.setTaskid(request.getTaskid());
			logTime(response);
		} catch (Throwable e) {
			logger.warn("channelRead0", e);
			ctx.channel().close();
		}
	}
	
	private void logTime(Response response) {
		// This is not the most correct time cost for each stage.
		logger.info("Total row count: " + response.totalCount + "  Decode/Execution/Encode: " + response.getDecodeRequestTime() + "/" + response.getDbTime() + "/" + response.getEncodeResponseTime());
		timeCostSender.execute(new TimeCostSendTask(response)); 
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
