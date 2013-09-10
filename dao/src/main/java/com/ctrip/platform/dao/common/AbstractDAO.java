package com.ctrip.platform.dao.common;

import java.sql.ResultSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dao.client.DALClient;
import com.ctrip.platform.dao.client.DBClient;
import com.ctrip.platform.dao.param.Parameter;

public class AbstractDAO  implements DAO {

	private static final Logger logger = LoggerFactory.getLogger(AbstractDAO.class);

	private boolean useDBClient;
	
	//all the sub class instance share the same instance of DBClient or DALClient
	private static DBClient dbClient;
	private static DALClient dalClient;

	public AbstractDAO() {

	}

	public boolean isUseDBClient() {
		return this.useDBClient;
	}

	public void setUseDBClient(boolean useDBClient) {
		this.useDBClient = useDBClient;
	}

	@Override
	public ResultSet fetch(String tnxCtxt, int flag, String statement,
			Parameter... params) throws Exception {
		if (useDBClient) {
			if (dbClient == null) {
				dbClient = new DBClient();
				dbClient.init();
			}
			return dbClient.fetch(tnxCtxt, flag, statement, params);
		} else {
			if (dalClient == null) {
				dalClient = new DALClient();
			}
			return dalClient.fetch(tnxCtxt, flag, statement, params);
		}
	}

	@Override
	public <T> List<T> fetchVO(String tnxCtxt, int flag, String statement,
			Parameter... params) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet fetchBySp(String tnxCtxt, int flag, String sp,
			Parameter... params) throws Exception {
		if (useDBClient) {
			if (dbClient == null) {
				dbClient = new DBClient();
				dbClient.init();
			}
			return dbClient.fetchBySp(tnxCtxt, flag, sp, params);
		} else {
			if (dalClient == null) {
				dalClient = new DALClient();
			}
			return dalClient.fetchBySp(tnxCtxt, flag, sp, params);
		}
	}

	@Override
	public <T> List<T> fetchVOBySp(String tnxCtxt, int flag, String sp,
			Parameter... params) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int execute(String tnxCtxt, int flag, String statement,
			Parameter... params) throws Exception {
		if (useDBClient) {
			if (dbClient == null) {
				dbClient = new DBClient();
				dbClient.init();
			}
			return dbClient.execute(tnxCtxt, flag, statement, params);
		} else {
			if (dalClient == null) {
				dalClient = new DALClient();
			}
			return dalClient.execute(tnxCtxt, statement, flag, params);
		}
	}

	@Override
	public int executeSp(String tnxCtxt, int flag, String sp,
			Parameter... params) throws Exception {
		if (useDBClient) {
			if (dbClient == null) {
				dbClient = new DBClient();
				dbClient.init();
			}
			return dbClient.executeSp(tnxCtxt, flag, sp, params);
		} else {
			if (dalClient == null) {
				dalClient = new DALClient();
			}
			return dalClient.executeSp(tnxCtxt, sp, flag, params);
		}
	}

}
