package com.ctrip.platform.dao;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.ctrip.platform.dao.client.Client;
import com.ctrip.platform.dao.client.DasClient;
import com.ctrip.platform.dao.param.StatementParameter;

public class AbstractDAO implements DAO {
	
	protected String logicDbName;
	
	protected int servicePort;
	
	protected String credentialId;
	
	private static Map<String, Client> clients = new ConcurrentHashMap<String, Client>();
	
	private static ReadWriteLock lock = new ReentrantReadWriteLock();
	
	protected void init(){
		lock.writeLock().lock();
		if(!clients.containsKey(logicDbName)){
			DasClient client = new DasClient();
			client.setCredentialId(credentialId);
			client.setLogicDbName(logicDbName);
			client.init("127.0.0.1", servicePort);
			clients.put(logicDbName, client);
		}
		lock.writeLock().unlock();
	}

	protected void init(String host){
		lock.writeLock().lock();
		if(!clients.containsKey(logicDbName)){
			DasClient client = new DasClient();
			client.setCredentialId(credentialId);
			client.setLogicDbName(logicDbName);
			client.init(host, servicePort);
			clients.put(logicDbName, client);
		}
		lock.writeLock().unlock();
	}

	@Override
	public ResultSet fetch(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		lock.readLock().lock();
		Client client = null;
		if(clients.containsKey(logicDbName)){
			client = clients.get(logicDbName);
		}
		lock.readLock().unlock();
		
		if(client != null){
			return client.fetch(sql, parameters, keywordParameters);
		}
		
		return null;
	}

	@Override
	public int execute(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		lock.readLock().lock();
		Client client = null;
		if(clients.containsKey(logicDbName)){
			client = clients.get(logicDbName);
		}
		lock.readLock().unlock();
		
		if(client != null){
			return client.execute(sql, parameters, keywordParameters);
		}
		
		return 0;
	}

	@Override
	public ResultSet fetchBySp(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int executeSp(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	

}
