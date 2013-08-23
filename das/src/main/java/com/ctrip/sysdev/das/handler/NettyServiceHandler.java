package com.ctrip.sysdev.das.handler;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.enums.ResultTypeEnum;
import com.ctrip.sysdev.das.request.DefaultRequest;
import com.ctrip.sysdev.das.response.DefaultResponse;

/**
 * 
 * @author weiw
 * 
 */
public class NettyServiceHandler extends SimpleChannelInboundHandler<DefaultRequest> {

	private static final Logger logger = LoggerFactory.getLogger(NettyServiceHandler.class);
	private ChannelGroup allChannels;

	public NettyServiceHandler(ChannelGroup allChannels) {
		this.allChannels = allChannels;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, DefaultRequest request) {
		try {
			logger.debug("channelRead0 from {} message = '{}'", ctx.channel(),
					request);
			processIncomingMessage(ctx.channel(), request);
		} catch (Exception e) {
			logger.warn("channelRead0", e);
			ctx.channel().close();
		}
	}

	private void processIncomingMessage(Channel channel,
			final DefaultRequest request) {
		// ..dboperator.....
		
		ByteBuf buf = Unpooled.buffer(10);
		
		buf.writeInt(26);
		buf.writeShort(1);
		
		DefaultResponse response = new DefaultResponse();
		response.setTaskid(request.getTaskid());
		response.setResultType(ResultTypeEnum.CUD);
		response.setAffectRowCount(1);
		try {
			buf.writeBytes(response.pack());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		channel.write(1);
//		channel.write(ResultType.CUD);
		ChannelFuture wf =  channel.writeAndFlush(buf);
		
		//ChannelFuture wf = channel.writeAndFlush(request);// 回写返回结果
		wf.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (!future.isSuccess()) {
					logger.error("server write response error,request = "
							+ request);
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
