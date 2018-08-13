package test.com.ctrip.platform.dal.dao.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Types;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.task.UpdateSqlTask;

public class UpdateSqlTaskTestStub extends TaskTestStub {
	
	public UpdateSqlTaskTestStub(String dbName) {
		super(dbName);
	}

	@Test
	public void testExecute() {
		UpdateSqlTask test = new UpdateSqlTask();
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.VARCHAR, "123456");
		parameters.set(2, Types.INTEGER, 1);
		
		try {
			int result = test.execute(getClient(), "update " + getParser().getTableName() + " set address=? where id=?", parameters, hints, null);
			assertEquals("123456", getDao().queryByPk(1, hints).getAddress());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
}
