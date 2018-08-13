package test.com.ctrip.platform.dal.dao.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import com.ctrip.platform.dal.dao.task.DefaultTaskContext;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.task.QuerySqlTask;

public class QuerySqlTaskTestStub extends TaskTestStub {
	
	public QuerySqlTaskTestStub(String dbName) {
		super(dbName);
	}

	@Test
	public void testExecute() {
		QuerySqlTask<List<ClientTestModel>> test = new QuerySqlTask<>(new DalRowMapperExtractor<ClientTestModel>(getParser()));
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.INTEGER, 1);
		
		try {
			List<ClientTestModel> result = test.execute(getClient(), "select * from " + getParser().getTableName() + " where id=?", parameters, hints, null);
			assertEquals(1, result.size());
			assertEquals(1, result.get(0).getId().intValue());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
}
