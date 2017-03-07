package test.com.ctrip.platform.dal.dao.unitbase;

public abstract class Script {
	 public abstract String createTable();
	 public abstract String dropTable();
	 public abstract String createSpInsert();
	 public abstract String dropSpInsert();
	 public abstract String createSpUpdate();
	 public abstract String dropSpUpdate();
	 public abstract String createSpDelete();
	 public abstract String dropSpDelete();
	 public abstract String[] mockData();
	 public abstract String getTableName();
	 
	 public String deleteTable(){
		 return "DELETE FROM " + this.getTableName();
	 }
	 
	 public String getSpInsertName(){
		 return this.getTableName() + "_test_i";
	 }
	 
	 public String getSpUpdateName(){
		 return this.getTableName() + "_test_u";
	 }
	 
	 public String getSpDeleteName(){
		 return this.getTableName() + "_test_d";
	 }
}
