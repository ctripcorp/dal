package com.ctrip.sysdev.das.netty4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Netty4Server {

	private static final Logger logger = LoggerFactory
			.getLogger(Netty4Server.class);

	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private String inetHost;
	private NioEventLoopGroup bossGroup;
	private NioEventLoopGroup ioGroup;
	private EventExecutorGroup businessGroup;
	private ChannelInitializer<Channel> channelInitializer;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject
	public Netty4Server(@Named("InetHost") String inetHost,
			@Named("bossGroup") NioEventLoopGroup bossGroup,
			@Named("ioGroup") NioEventLoopGroup ioGroup,
			@Named("businessGroup") EventExecutorGroup businessGroup,
			ChannelInitializer channelInitializer) {
		this.inetHost = inetHost;
		this.bossGroup = bossGroup;
		this.ioGroup = ioGroup;
		this.channelInitializer = channelInitializer;
		this.businessGroup = businessGroup;
	}

	public void start(int inetPort) throws Exception {
		if (!startFlag.compareAndSet(false, true)) {
			return;
		}
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, ioGroup)
				.channel(NioServerSocketChannel.class)
				.childOption(
						ChannelOption.TCP_NODELAY,
						Boolean.parseBoolean(System.getProperty(
								"dal.tcp.nodelay", "true")))
				.childOption(
						ChannelOption.SO_REUSEADDR,
						Boolean.parseBoolean(System.getProperty(
								"dal.tcp.reuseaddress", "true")))
				.childHandler(channelInitializer);
		b.bind(inetHost, inetPort).sync();
		logger.info("Server started,listen at: " + inetHost + "," + inetPort);
	}

	public void stop() throws Exception {
		bossGroup.shutdownGracefully().await();
		ioGroup.shutdownGracefully().await();
		businessGroup.shutdownGracefully().await();;
		logger.warn("Server stop!");
		startFlag.set(false);
	}

}
