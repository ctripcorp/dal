package DAL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;

public class PersonTestDao {
	private static final String DATA_BASE = "PerformanceTest";
	private DalQueryDao queryDao;

	private JustATestPojoRowMapper justATestPojoRowMapper = new JustATestPojoRowMapper();

	public PersonTestDao() {
		queryDao = new DalQueryDao(DATA_BASE);
	}
    
	public List<JustATestPojo> justATest(Integer id) throws SQLException {
		String sql = "select * from person where id = @id";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();

		int i = 1;
		parameters.set(i++, Types.INTEGER, id);

		//如果只需要一条记录，建议使用limit 1或者top 1，并使用SelectFirst提高性能
		return queryDao.query(sql, parameters, hints, justATestPojoRowMapper);
	}


	private class JustATestPojoRowMapper implements DalRowMapper<JustATestPojo> {

		@Override
		public JustATestPojo map(ResultSet rs, int rowNum) throws SQLException {
			JustATestPojo pojo = new JustATestPojo();
			
			pojo.setID((Integer)rs.getObject("ID"));
			pojo.setName((String)rs.getObject("Name"));
			pojo.setAge((Integer)rs.getObject("Age"));
			pojo.setBirth((Timestamp)rs.getObject("Birth"));

			return pojo;
		}
	}

}
