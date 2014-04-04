package com.ctrip.dal.test.test3;

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

public class PersonDaoDao {
	private static final String DATA_BASE = "dao_test";
	private DalQueryDao queryDao;

	private GetPersonByAddrAndTelPojoRowMapper getPersonByAddrAndTelPojoRowMapper = new GetPersonByAddrAndTelPojoRowMapper();

	public PersonDaoDao() {
		queryDao = new DalQueryDao(DATA_BASE);
	}
    
	public List<GetPersonByAddrAndTelPojo> getPersonByAddrAndTel(String param1, String param2) throws SQLException {
		String sql = "select t.Address,t.Age,t.Telephone,t.Birth,t.Gender from Person t WHERE t.Address=? and t.Telephone=?";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();

		int i = 1;
		parameters.set(i++, Types.VARCHAR, param1);
		parameters.set(i++, Types.VARCHAR, param2);

		//如果只需要一条记录，建议使用limit 1或者top 1，并使用SelectFirst提高性能
		return queryDao.query(sql, parameters, hints, getPersonByAddrAndTelPojoRowMapper);
	}


	private class GetPersonByAddrAndTelPojoRowMapper implements DalRowMapper<GetPersonByAddrAndTelPojo> {

		@Override
		public GetPersonByAddrAndTelPojo map(ResultSet rs, int rowNum) throws SQLException {
			GetPersonByAddrAndTelPojo pojo = new GetPersonByAddrAndTelPojo();
			
			pojo.setAddress((String)rs.getObject("Address"));
			pojo.setAge((Integer)rs.getObject("Age"));
			pojo.setTelephone((String)rs.getObject("Telephone"));
			pojo.setBirth((Timestamp)rs.getObject("Birth"));
			pojo.setGender((Integer)rs.getObject("Gender"));

			return pojo;
		}
	}

}
