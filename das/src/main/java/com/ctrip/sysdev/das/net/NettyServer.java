package com.ctrip.sysdev.das.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.MultithreadEventExecutorGroup;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ctrip.sysdev.das.common.Server;

/**
 * 
 * @author weiw
 * 
 */
public class NettyServer implements Server {

	private final ChannelGroup allChannels = new DefaultChannelGroup(
			GlobalEventExecutor.INSTANCE);
	private final int CPUCNT = Runtime.getRuntime().availableProcessors();
	private final AtomicBoolean STARTED = new AtomicBoolean(true);
	private final EventLoopGroup bossGroup = new NioEventLoopGroup();
	private final EventLoopGroup workerGroup = new NioEventLoopGroup(CPUCNT - 1);
	private final MultithreadEventExecutorGroup logicGroup = new DefaultEventExecutorGroup(
			CPUCNT * 5);

	private String inetHost;
	private int inetPort;

	public NettyServer(String inetHost, int inetPort) {
		this.inetHost = inetHost;
		this.inetPort = inetPort;
	}

	@Override
	public void start() {
		try {
			InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
			final ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ServerChannelInitializer())
					.option(ChannelOption.SO_BACKLOG, 128)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.SO_REUSEADDR, true)
					.childOption(ChannelOption.TCP_NODELAY, true)
					.childOption(ChannelOption.SO_REUSEADDR, true);
			final Channel serverChannel = b.bind(inetHost, inetPort).sync()
					.channel();
			allChannels.add(serverChannel);
		} catch (Exception e) {
		}
	}

	private class ServerChannelInitializer extends ChannelInitializer<Channel> {
		@Override
		protected void initChannel(Channel ch) throws Exception {
			final ChannelPipeline p = ch.pipeline();
			p.addLast("logger", new LoggingHandler(LogLevel.DEBUG));
			p.addLast("decoder", new MessageDecode());
			p.addLast("encoder", new MessageEncoder());
			p.addLast(logicGroup, "handler", new Handler(allChannels));
		}
	}

	@Override
	public boolean isStarted() {
		return STARTED.get();
	}

	@Override
	public void stop() {
		allChannels.close();
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		STARTED.set(false);
	}
}
