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
public class PooledSocket implements Closeable, Recycleable {

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
	
	public DataInputStream getIn() {
		return in;
	}

	public void setIn(DataInputStream in) {
		this.in = in;
	}

	public DataOutputStream getOut() {
		return out;
	}

	public void setOut(DataOutputStream out) {
		this.out = out;
	}

	public boolean isAlive(){
		boolean result =  socket != null && socket.isConnected();
		
		try {
			out.writeInt(2);
			out.writeShort(-1);
			result = result && true;
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public void close() throws IOException {
//		this.out.close();
//		this.in.close();
		this.socket.close();
	}

	@Override
	public void recycle(Closeable closeable) throws IllegalArgumentException,
			IOException {
		this.pool.recycle(this);
	}
	
	

}
