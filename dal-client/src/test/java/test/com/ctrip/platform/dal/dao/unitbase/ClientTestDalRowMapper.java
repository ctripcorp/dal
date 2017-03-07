package test.com.ctrip.platform.dal.dao.unitbase;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalRowMapper;

public class ClientTestDalRowMapper  implements DalRowMapper<ClientTestModel>{
	@Override
	public ClientTestModel map(ResultSet rs, int rowNum)
			throws SQLException {
		ClientTestModel model = new ClientTestModel();
		model.setId(rs.getInt(1));
		model.setQuantity(rs.getInt(2));
		model.setType(rs.getShort(3));
		model.setAddress(rs.getString(4));
		model.setLastChanged(rs.getTimestamp(5));
		return model;
	}
}
