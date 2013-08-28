package com.ctrip.sysdev.das.worker;

import io.netty.channel.Channel;

import com.ctrip.sysdev.das.domain.Request;

public class ChannelRequest {
	Channel channel;
	Request request;
	ChannelRequest(Channel channel, Request request) {
		this.channel = channel;
		this.request = request;
	}
}
