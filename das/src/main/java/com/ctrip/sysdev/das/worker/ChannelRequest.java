package com.ctrip.sysdev.das.worker;

import io.netty.channel.Channel;

import com.ctrip.sysdev.das.request.DefaultRequest;

public class ChannelRequest {
	Channel channel;
	DefaultRequest request;
	ChannelRequest(Channel channel, DefaultRequest request) {
		this.channel = channel;
		this.request = request;
	}
}
