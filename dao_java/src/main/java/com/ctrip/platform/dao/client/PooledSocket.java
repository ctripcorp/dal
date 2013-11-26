package com.ctrip.platform.dao.client;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 * @author gawu
 *
 */
public class PooledSocket implements Closeable {

	public PooledSocket(SocketPool socketPool, String ip, int port,
			int sendReceiveTimeout, int connectTimeout) throws UnknownHostException, IOException {
		
		pool = socketPool;
		socket = new Socket();
		
		// Do not use Nagle's Algorithm
		socket.setTcpNoDelay(true);;
		socket.setSoTimeout(sendReceiveTimeout);
		socket.connect(new InetSocketAddress(ip, port), connectTimeout);
		
		out = new DataOutputStream(socket.getOutputStream());

		in = new DataInputStream(socket.getInputStream());
	}

	private SocketPool pool;

	private Socket socket;
	
	private DataInputStream in;
	
	private DataOutputStream out;
	
	public boolean isAlive(){
		return socket != null && socket.isConnected();
	}

	@Override
	public void close() throws IOException {
		
	}
	
	

}
