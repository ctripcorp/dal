package com.ctrip.sysdev.das.common.ctrl;

public final class PortHandler {
	private int port;
	private MessageReciver reciver;
	
	public PortHandler(int port, MessageReciver reciver) {
		this.port = port;
		this.reciver = reciver;
	}
	
	/**
	 * Used for controller. it will wait worker connect to it
	 */
	public void waitForConnect() {
		//
	}
	
	public boolean isConnected() {
		return false;
	}
	
	/**
	 * Used for worker. to connect to controller
	 * @param url
	 * @param port
	 * @return
	 */
	public boolean connect(String url, int port) {
		return true;
	}
	
	public void sendMessage(Message msg, Listener listener ) {
		//
	}

	public Message sendMessageBlocked(Message msg) {
		return null;
	}
	
	private void messageRecived(Message msg) {
		reciver.recive(msg);
	}
}
