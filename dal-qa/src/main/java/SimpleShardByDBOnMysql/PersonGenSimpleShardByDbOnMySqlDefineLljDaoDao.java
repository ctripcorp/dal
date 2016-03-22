package SimpleShardByDBOnMysql;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import com.ctrip.platform.dal.dao.helper.*;

public class PersonGenSimpleShardByDbOnMySqlDefineLljDaoDao {

	private static final String DATA_BASE = "SimpleShardByDBOnMysql";
	
	private DalQueryDao queryDao = null;
	private DalClient baseClient = null;

	private DalRowMapper<PersonGenSimpleShardByDbOnMySqlDefineLljPojo> personGenSimpleShardByDbOnMySqlDefineLljPojoRowMapper = null;

	public PersonGenSimpleShardByDbOnMySqlDefineLljDaoDao() throws SQLException {
		this.personGenSimpleShardByDbOnMySqlDefineLljPojoRowMapper = new DalDefaultJpaMapper(PersonGenSimpleShardByDbOnMySqlDefineLljPojo.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
	/**
	 * 查询
	**/
	public List<PersonGenSimpleShardByDbOnMySqlDefineLljPojo> test_def_query(Integer Age, DalHints hints) throws SQLException {
		String sql = "select * from person where Age=?";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.setSensitive(i++, "Age", Types.INTEGER, Age);
		return (List<PersonGenSimpleShardByDbOnMySqlDefineLljPojo>)queryDao.query(sql, parameters, hints, personGenSimpleShardByDbOnMySqlDefineLljPojoRowMapper);
	}
	/**
	 * 增删改
	**/
	public int test_def_update (DalHints hints) throws SQLException {
		String sql = SQLParser.parse("truncate Person");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		
		int i = 1;
		return baseClient.update(sql, parameters, hints);
	}
}
