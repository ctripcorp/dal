package com.ctrip.sysdev.das.netty4;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.EventExecutorGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.domain.Request;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class Netty4ChannelInitializer extends ChannelInitializer<Channel> {

	private static final Logger logger = LoggerFactory
			.getLogger(Netty4ChannelInitializer.class);

	@Inject
	private Provider<ByteToMessageDecoder> netty4ProtocolDecoderProvider;
	@SuppressWarnings("rawtypes")
	@Inject
	private Provider<MessageToByteEncoder> netty4ProtocolEncoderProvider;
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
		ByteToMessageDecoder netty4ProtocolDecoder = netty4ProtocolDecoderProvider
				.get();
		MessageToByteEncoder<Object> netty4ProtocolEncoder = netty4ProtocolEncoderProvider
				.get();
		SimpleChannelInboundHandler<Request> netty4Handler = netty4HandlerProvider
				.get();

		p.addLast("logger", new LoggingHandler(LogLevel.DEBUG));
		p.addLast("decoder", netty4ProtocolDecoder);
		p.addLast("encoder", netty4ProtocolEncoder);
		p.addLast(businessGroupProvider.get(), "handler", netty4Handler);

	}

}
