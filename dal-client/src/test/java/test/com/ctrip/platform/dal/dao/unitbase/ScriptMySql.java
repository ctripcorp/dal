package test.com.ctrip.platform.dal.dao.unitbase;


public class ScriptMySql extends Script{
	
	private static final String CREATE_TABLE = "CREATE TABLE %s("
			+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
			+ "type smallint, "
			+ "address VARCHAR(64) not null, "
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	private static final String DROP_TABLE = "DROP TABLE IF EXISTS %s";
	private static final String CREATE_I_PROCEDURE = "CREATE PROCEDURE %s("
			+ "dal_id int,"
			+ "quantity int,"
			+ "type smallint,"
			+ "address VARCHAR(64)) "
			+ "BEGIN INSERT INTO %s"
			+ "(id, quantity, type, address) "
			+ "VALUES(dal_id, quantity, type, address);"
			+ "SELECT ROW_COUNT() AS result;"
			+ "END";
	private static final String CREATE_D_PROCEDURE = "CREATE PROCEDURE %s("
			+ "dal_id int,"
			+ "out count int)"
			+ "BEGIN DELETE FROM %s WHERE id=dal_id;"
			+ "SELECT ROW_COUNT() AS result;"
			+ "SELECT COUNT(*) INTO count from %s;"
			+ "END";
	private static final String CREATE_U_PROCEDURE = "CREATE PROCEDURE %s("
			+ "dal_id int,"
			+ "quantity int,"
			+ "type smallint,"
			+ "INOUT address VARCHAR(64))"
			+ "BEGIN UPDATE %s "
			+ "SET quantity = quantity, type=type, address=address "
			+ "WHERE id=dal_id;"
			+ "SELECT ROW_COUNT() AS result;"
			+ "END";
	private static final String DROP_PROCEDURE = "DROP PROCEDURE  IF  EXISTS %s";
	
	private String tableName;
	
	public ScriptMySql(String tableName){
		this.tableName = tableName;
	}
	
	@Override
	public String createTable() {
		return String.format(CREATE_TABLE, this.tableName);
	}

	@Override
	public String dropTable() {
		return String.format(DROP_TABLE, this.tableName);
	}

	@Override
	public String createSpInsert() {
		return String.format(CREATE_I_PROCEDURE, 
				this.getSpInsertName(), this.getTableName());
	}

	@Override
	public String dropSpInsert() {
		return String.format(DROP_PROCEDURE, this.getSpInsertName());
	}

	@Override
	public String createSpUpdate() {
		return String.format(CREATE_U_PROCEDURE, this.getSpUpdateName(), 
				this.getTableName());
	}

	@Override
	public String dropSpUpdate() {
		return String.format(DROP_PROCEDURE, this.getSpUpdateName());
	}

	@Override
	public String createSpDelete() {
		return String.format(CREATE_D_PROCEDURE, this.getSpDeleteName(), 
				this.getTableName(), this.getTableName());
	}

	@Override
	public String dropSpDelete() {
		return String.format(DROP_PROCEDURE, this.getSpDeleteName());
	}

	@Override
	public String[] mockData() {
		String[] insertSqls = new String[] {
				"INSERT INTO " + this.tableName
						+ " VALUES(1, 10, 1, 'SH INFO', NULL)",
				"INSERT INTO " + this.tableName
						+ " VALUES(2, 11, 1, 'BJ INFO', NULL)",
				"INSERT INTO " + this.tableName
						+ " VALUES(3, 12, 2, 'SZ INFO', NULL)" };
		return insertSqls;
	}

	@Override
	public String getTableName() {
		return this.tableName;
	}
}
