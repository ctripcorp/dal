package test.com.ctrip.platform.dal.dao.unitbase;


public class ScriptSqlServer extends Script{
	
	private static final String CREATE_TABLE = "CREATE TABLE %s("
			+ "Id int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
			+ "quantity int,type smallint, "
			+ "address varchar(64) not null,"
			+ "last_changed datetime default getdate())";		
	private static final String DROP_TABLE = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '%s') "
			+ "DROP TABLE  %s";
	private static final String CREATE_I_PROCEDURE = "CREATE PROCEDURE %s("
			+ "@quantity int,"
			+ "@type smallint,"
			+ "@address VARCHAR(64)) "
			+ "AS BEGIN INSERT INTO %s "
			+ "([quantity], [type], [address]) "
			+ "VALUES(@quantity, @type, @address);"
			+ "RETURN @@ROWCOUNT;"
			+ "END";
	private static final String CREATE_D_PROCEDURE = "CREATE PROCEDURE %s("
			+ "@dal_id int,"
			+ "@count int OUTPUT)"
			+ "AS BEGIN DELETE FROM %s "
			+ "WHERE [id]=@dal_id;"
			+ "SELECT @count = COUNT(*) from %s;"
			+ "RETURN @@ROWCOUNT;"
			+ "END";	
	private static final String CREATE_U_PROCEDURE = "CREATE PROCEDURE %s("
			+ "@dal_id int,"
			+ "@quantity int,"
			+ "@type smallint,"
			+ "@last_changed datetime,"
			+ "@address VARCHAR(64) OUTPUT)"
			+ "AS BEGIN UPDATE %s "
			+ "SET [quantity] = @quantity, [type]=@type, [address]=@address, [last_changed]=@last_changed "
			+ "WHERE [id]=@dal_id;"
			+ "RETURN @@ROWCOUNT;"
			+ "END";
	private static final String DROP_PROCEDURE = "IF OBJECT_ID('dbo.%s') IS NOT NULL "
			+ "DROP PROCEDURE dbo.%s";
	
	private String tableName;
	
	public ScriptSqlServer(String tableName){
		this.tableName = tableName;
	}

	@Override
	public String createTable() {
		return String.format(CREATE_TABLE, 
				this.getTableName());
	}

	@Override
	public String dropTable() {
		return String.format(DROP_TABLE, 
				this.getTableName(), this.getTableName());
	}

	@Override
	public String createSpInsert() {
		return String.format(CREATE_I_PROCEDURE, 
				this.getSpInsertName(), this.getTableName());
	}

	@Override
	public String dropSpInsert() {
		return String.format(DROP_PROCEDURE, 
				this.getSpInsertName(), this.getSpInsertName());
	}

	@Override
	public String createSpUpdate() {
		return String.format(CREATE_U_PROCEDURE, 
				this.getSpUpdateName(), this.getTableName());
	}

	@Override
	public String dropSpUpdate() {
		return String.format(DROP_PROCEDURE, 
				this.getSpUpdateName(), this.getSpUpdateName());
	}

	@Override
	public String createSpDelete() {
		return String.format(CREATE_D_PROCEDURE, 
				this.getSpDeleteName(), this.getTableName(), 
				this.getTableName());
	}

	@Override
	public String dropSpDelete() {
		return String.format(DROP_PROCEDURE, 
				this.getSpDeleteName(), this.getSpDeleteName());
	}

	@Override
	public String[] mockData() {
		String[] scripts = new String[] {
				"SET IDENTITY_INSERT "+ this.getTableName() +" ON",
				"INSERT INTO " + this.getTableName() + "(Id, quantity,type,address)"
						+ " VALUES(1, 10, 1, 'SH INFO')",
				"INSERT INTO " + this.getTableName() + "(Id, quantity,type,address)"
						+ " VALUES(2, 11, 1, 'BJ INFO')",
				"INSERT INTO " + this.getTableName() + "(Id, quantity,type,address)"
						+ " VALUES(3, 12, 2, 'SZ INFO')",
				"SET IDENTITY_INSERT "+ this.getTableName() +" OFF"};
		return scripts;
	}

	@Override
	public String getTableName() {
		return this.tableName;
	}

}
