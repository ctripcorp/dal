package com.ctrip.sysdev.das.netty4;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.EventExecutorGroup;

import com.ctrip.sysdev.das.domain.DasProto;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class Netty4ChannelInitializer extends ChannelInitializer<Channel> {
	@SuppressWarnings("rawtypes")
	@Inject
	private Provider<SimpleChannelInboundHandler> netty4HandlerProvider;
	@Inject
	@Named("businessGroup")
	private Provider<EventExecutorGroup> businessGroupProvider;

	@SuppressWarnings({ "unchecked" })
	@Override
	protected void initChannel(Channel ch) throws Exception {

		ChannelPipeline p = ch.pipeline();
		SimpleChannelInboundHandler<DasProto.Request> netty4Handler = netty4HandlerProvider
				.get();

		p.addLast("logger", new LoggingHandler(LogLevel.DEBUG));
		p.addLast("decoder", new RequestDecoder());
		p.addLast(businessGroupProvider.get(), "handler", netty4Handler);

	}
}
