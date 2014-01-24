package com.ctrip.sysdev.das.guice;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.netty4.Netty4ChannelInitializer;
import com.ctrip.sysdev.das.netty4.DalServiceHandler;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

public class NettyModule extends AbstractModule {

	private static final Logger logger = LoggerFactory
			.getLogger(NettyModule.class);

	private final int CPUCNT = Runtime.getRuntime().availableProcessors();

	@Override
	protected void configure() {
		bind(SimpleChannelInboundHandler.class).to(DalServiceHandler.class);
		bind(ChannelInitializer.class).to(Netty4ChannelInitializer.class).in(
				Scopes.SINGLETON);
		logger.info("NettyModule loaded");
	}

	@Provides
	@Singleton
	@Named("bossGroup")
	NioEventLoopGroup provideBossGroup() {
		return new NioEventLoopGroup();
	}

	@Provides
	@Singleton
	@Named("ioGroup")
	NioEventLoopGroup provideIoGroup() {
		return new NioEventLoopGroup();
	}

	@Provides
	@Singleton
	@Named("businessGroup")
	EventExecutorGroup provideBusinessGroup() {
		return new DefaultEventExecutorGroup(CPUCNT * 5);
	}

	@Provides
	@Singleton
	@Named("ChannelGroup")
	ChannelGroup provideChannelGroup() {
		ChannelGroup channelGroup = new DefaultChannelGroup(
				GlobalEventExecutor.INSTANCE);
		return channelGroup;
	}

}
