package test.com.ctrip.platform.dal.dao.unittests;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.com.ctrip.platform.dal.dao.unitbase.ClientTestDalRowMapper;
import test.com.ctrip.platform.dal.dao.unitbase.ClientTestModel;
import test.com.ctrip.platform.dal.dao.unitbase.MySqlDatabaseInitializer;

import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;

/**
 * Unit tests for Dal direct client
 * 
 * @author wcyuan
 * @version 2014-05-04
 */
public class DalDirectClientMySqlTest extends DalDirectClientTestStub {
	private static MySqlDatabaseInitializer initializer = new MySqlDatabaseInitializer();
	public DalDirectClientMySqlTest() {
		super(initializer.DATABASE_NAME, initializer.diff);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		initializer.setUpBeforeClass();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		initializer.tearDownAfterClass();
	}

	@Before
	public void setUp() throws Exception {
		initializer.setUp();
	}

	@After
	public void tearDown() throws Exception {
		initializer.tearDown();
	}
}
