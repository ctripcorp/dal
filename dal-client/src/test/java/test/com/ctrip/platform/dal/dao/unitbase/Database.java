package test.com.ctrip.platform.dal.dao.unitbase;

import java.sql.SQLException;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;

public class Database {
	
	private String tableName = null;
	private String databaseName = null;
	private DalClient client = null;
	private DalRowMapper<?> mapper = null;
	private Script initScript = null;
	
	public Database(String databaseName, String tableName, DatabaseCategory dbType){
		this.databaseName = databaseName;
		this.tableName = tableName;
		this.initScript = DatabaseCategory.SqlServer == dbType ?
				new ScriptSqlServer(this.tableName) : new ScriptMySql(this.tableName);
		this.client = DalClientFactory.getClient(this.databaseName);
		this.mapper = new ClientTestDalRowMapper();
	}
	
	static{
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void init() throws SQLException{
		this.batchUpdate(this.initScript.dropTable(), this.initScript.createTable(), 
				this.initScript.dropSpInsert(), this.initScript.createSpInsert(),
				this.initScript.dropSpUpdate(), this.initScript.createSpUpdate(),
				this.initScript.dropSpDelete(), this.initScript.createSpDelete());
	}
	
	public void drop() throws SQLException{
		this.batchUpdate(this.initScript.dropTable(),this.initScript.dropSpDelete(),
				this.initScript.dropSpInsert(), this.initScript.dropSpUpdate());
	}
	
	public void mock() throws SQLException{
		this.client.batchUpdate(this.initScript.mockData(), new DalHints());
	}
	
	public void clear() throws SQLException{
		this.batchUpdate(this.initScript.deleteTable());
	}
	
	public String getTableName() {
		return tableName;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public DalClient getClient() {
		return client;
	}
	
	public DalRowMapper<?> getMapper() {
		return mapper;
	}

	public void setMapper(DalRowMapper<?> mapper) {
		this.mapper = mapper;
	}

	private void batchUpdate(String... sqls) throws SQLException{
		DalHints hints = new DalHints();
		hints.set(DalHintEnum.operation, DalEventEnum.UPDATE_SIMPLE);
		StatementParameters parameters = new StatementParameters();
		for (String sql : sqls) {
			try {
				this.client.update(sql, parameters, hints);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
