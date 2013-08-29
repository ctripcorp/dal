package {{product_line}}.{{domain}}.{{app_name}}.dao.common;

import java.sql.ResultSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import {{product_line}}.{{domain}}.{{app_name}}.dao.client.DALClient;
import {{product_line}}.{{domain}}.{{app_name}}.dao.client.DBClient;
import {{product_line}}.{{domain}}.{{app_name}}.dao.msg.AvailableType;

public class AbstractDAO { // implements IDAO {

	/*
	 * Free Form SQL Example:
	 * 
	 * SELECT Address, Telephone FROM Person
	 * 
	 * WHERE Name = {string} AND Age BETWEEN {int} AND {int}
	 * 
	 * AND Gender IN {int[]}
	 */

	private static final Logger logger = LoggerFactory.getLogger(AbstractDAO.class);

	private boolean useDBClient;
	private DBClient dbClient;
	private DALClient dalClient;

	public AbstractDAO() {

	}

	public boolean isUseDBClient() {
		return this.useDBClient;
	}

	public void setUseDBClient(boolean useDBClient) {
		this.useDBClient = useDBClient;
	}

	public ResultSet fetch(String tnxCtxt, String statement, int flag,
			AvailableType... params) throws Exception {

		// all the parameters required are now provided
		if (useDBClient) {
			if (dbClient == null) {
				dbClient = new DBClient();
				dbClient.init();
			}
			return dbClient.fetch(null, statement, 0, params);
		} else {

		}

		return null;
	}

	public <T> List<T> fetchByORM(String tnxCtxt, String statement,
			List<AvailableType> params, int flag) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSet fetchBySp(String tnxCtxt, String sp, int flag,
			AvailableType... params) throws Exception {
		// all the parameters required are now provided
		if (useDBClient) {
			if (dbClient == null) {
				dbClient = new DBClient();
				dbClient.init();
			}
			return dbClient.fetchBySp(null, sp, 0, params);
		} else {

		}

		return null;
	}

	public <T> List<T> fetchBySpByORM(String tnxCtxt, String sp,
			List<AvailableType> params, int flag) {
		// TODO Auto-generated method stub
		return null;
	}

	public int execute(String tnxCtxt, String statement, int flag,
			AvailableType... params) throws Exception {

		// all the parameters required are now provided
		if (useDBClient) {
			if (dbClient == null) {
				dbClient = new DBClient();
				dbClient.init();
			}
			return dbClient.execute(null, statement, flag, params);
		} else {
			if(dalClient == null){
				dalClient = new DALClient();
			}
			return dalClient.execute(null, statement, flag, params);
		}
	}

	public int executeSp(String tnxCtxt, String sp, int flag,
			AvailableType... params) throws Exception {
		// all the parameters required are now provided
		if (useDBClient) {
			if (dbClient == null) {
				dbClient = new DBClient();
				dbClient.init();
			}
			return dbClient.executeSp(tnxCtxt, sp, flag, params);
		} else {

		}

		return 0;
	}

	public int bulkInsert(String tnxCtxt, String statement,
			List<AvailableType> params, int flag) {
		// TODO Auto-generated method stub
		return 0;
	}
}
