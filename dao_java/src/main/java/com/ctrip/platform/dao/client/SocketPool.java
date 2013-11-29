package com.ctrip.platform.dao.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ctrip.platform.dao.Recycleable;

/**
 * Manage a pool of socket
 * @author gawu
 *
 */
public class SocketPool implements Recycleable{

	SocketPool(String host, int port) {
		queue = new ConcurrentLinkedQueue<PooledSocket>();
		this.host = host;
		this.port = port;
	}

	private ConcurrentLinkedQueue<PooledSocket> queue;

	private int sendReceiveTimeout = 30000;
	private int connectTimeout = 30000;
	private int maxPoolSize = 10;

	private String host;

	private int port;

	/**
	 * Get a socket from the pool, if none available, create a new one
	 * @return the socket acquired
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public PooledSocket acquire() throws UnknownHostException, IOException {

		PooledSocket current = null;

		// Thread safe here
		while ((current = queue.poll()) != null) {
			if (current.isAlive()) {
				return current;
			}
		}

		PooledSocket sock = new PooledSocket(this, host, port,
				sendReceiveTimeout, connectTimeout);

		return sock;

	}
	
	/**
	 * Recycle the pooled socket, if necessary, reserve for future use 
	 * @param sock
	 * @throws IOException 
	 */
	@Override
	public void recycle(Closeable closeable)  throws IllegalArgumentException, IOException{
		if(!(closeable instanceof PooledSocket)){
			throw new IllegalArgumentException("Expect PooledSocket instance but get "+closeable.getClass().getName());
		}
		PooledSocket sock = (PooledSocket)closeable;
		if(!sock.isAlive()){
			sock.close();
		}else{
			//TODO: First reset the socket
			
			if(queue.size() > maxPoolSize){
				sock.close();
			}else{
				queue.add(sock);
			}
		}
	}

}
