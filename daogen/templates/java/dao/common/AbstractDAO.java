package {{product_line}}.{{domain}}.{{app_name}}.dao.common;

import java.sql.ResultSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import {{product_line}}.{{domain}}.{{app_name}}.dao.client.DALClient;
import {{product_line}}.{{domain}}.{{app_name}}.dao.client.DBClient;
import {{product_line}}.{{domain}}.{{app_name}}.dao.param.Parameter;

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
			Parameter... params) throws Exception {

		// all the parameters required are now provided
		if (useDBClient) {
			if (dbClient == null) {
				dbClient = new DBClient();
				dbClient.init();
			}
			return dbClient.fetch(tnxCtxt, statement, flag, params);
		} else {
			if(dalClient == null){
				dalClient = new DALClient();
			}
			return dalClient.fetch(tnxCtxt, statement, flag, params);

		}

//		return null;
	}

	public <T> List<T> fetchByORM(String tnxCtxt, String statement,
			List<Parameter> params, int flag) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSet fetchBySp(String tnxCtxt, String sp, int flag,
			Parameter... params) throws Exception {
		// all the parameters required are now provided
		if (useDBClient) {
			if (dbClient == null) {
				dbClient = new DBClient();
				dbClient.init();
			}
			return dbClient.fetchBySp(null, sp, 0, params);
		} else {
			if(dalClient == null){
				dalClient = new DALClient();
			}
			return dalClient.fetchBySp(tnxCtxt, sp, flag, params);

		}

//		return null;
	}

	public <T> List<T> fetchBySpByORM(String tnxCtxt, String sp,
			List<Parameter> params, int flag) {
		// TODO Auto-generated method stub
		return null;
	}

	public int execute(String tnxCtxt, String statement, int flag,
			Parameter... params) throws Exception {

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
			Parameter... params) throws Exception {
		// all the parameters required are now provided
		if (useDBClient) {
			if (dbClient == null) {
				dbClient = new DBClient();
				dbClient.init();
			}
			return dbClient.executeSp(tnxCtxt, sp, flag, params);
		} else {
			if(dalClient == null){
				dalClient = new DALClient();
			}
			return dalClient.executeSp(tnxCtxt, sp, flag, params);
		}

//		return 0;
	}

	public int bulkInsert(String tnxCtxt, String statement,
			List<Parameter> params, int flag) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void main(String[] args) throws Exception {

		// new BaseDAO().fetch(null, null, null, (Flags.TEST.getIntVal() |
		// Flags.COMMIT.getIntVal()));
		// new BaseDAO().fetch(null, null, null,Flags.COMMIT.getIntVal());
		// new BaseDAO().fetch(null, null, null,Flags.TEST.getIntVal());

		// Statement stmt;
		//
		// Class.forName("com.mysql.jdbc.Driver");
		//
		// String url = "jdbc:mysql://192.168.83.132:3306/dao_test";
		//
		// Connection conn = DriverManager.getConnection(url, "root", "123456");
		//
		// stmt = conn.createStatement();
		//
		// //PreparedStatement ps =
		// conn.prepareStatement("SELECT * FROM Person WHERE Gender = ?");
		//
		// PreparedStatement ps =
		// conn.prepareStatement("SELECT * FROM Person WHERE Gender in ?");
		//
		// //ps.setInt(1, 1);
		// Date d = new Date();
		//
		// System.out.println(d.toString());
		//
		// ResultSet rs = ps.executeQuery();
		//
		// //ResultSet rs = stmt.executeQuery("SELECT * FROM Person");
		//
		// ResultSetMetaData rsmd = rs.getMetaData();
		//
		// int totalColumns = rsmd.getColumnCount();
		//
		// int[] colTypes = new int[totalColumns];
		//
		// for (int i = 1; i <= totalColumns; i++) {
		// int currentColType = rsmd.getColumnType(i);
		// colTypes[i - 1] = currentColType;
		// }
		//
		// while (rs.next()) {
		// for (int i = 1; i <= totalColumns; i++) {
		// switch (colTypes[i-1]) {
		// case java.sql.Types.INTEGER:
		// System.out.println(rs.getInt(i));
		// break;
		// case java.sql.Types.VARCHAR:
		// System.out.println(rs.getString(i));
		// break;
		// default:
		// System.out.println("---------begin default----------");
		// System.out.println(rs.getObject(i));
		// System.out.println("----------end default----------");
		// break;
		// }
		// }
		// }

	}

}
