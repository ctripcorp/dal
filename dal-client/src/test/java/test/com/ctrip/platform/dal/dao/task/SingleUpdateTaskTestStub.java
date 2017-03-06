package test.com.ctrip.platform.dal.dao.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.task.SingleUpdateTask;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class SingleUpdateTaskTestStub extends TaskTestStub {
	private boolean isMySql;
	public SingleUpdateTaskTestStub (String dbName) {
		super(dbName);
	}

	public void setMySql(boolean value) {
		isMySql = value;
	}

	private void assertIntEquals(int exp, int act) {
		if(isMySql)
			Assert.assertEquals(exp, act);
	}
	
	@Test
	public void testExecute() {
		SingleUpdateTask<ClientTestModel> test = new SingleUpdateTask<>();
		test.initialize(getParser());
		DalHints hints = new DalHints();
		
		try {
			ClientTestModel model = getAll().get(0);
			model.setAddress("1122334455");
			
			int result = test.execute(hints, getParser().getFields(model));
			assertIntEquals(1, result);
			model = getDao().queryByPk(model, new DalHints());
			assertEquals("1122334455", model.getAddress());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testVersionNotSet() throws SQLException {
		SingleUpdateTask<UpdatableVersionModel> test = new SingleUpdateTask<>();
		DalParser<UpdatableVersionModel> parser = new DalDefaultJpaParser<>(UpdatableVersionModel.class, getDbName());
		test.initialize(parser);
		DalHints hints = new DalHints();
		
		ClientTestModel model = getAll().get(0);
		model.setAddress("1122334455");
		model.setLastChanged(null);
		
		try {
			test.execute(hints, getParser().getFields(model));
			fail();
		} catch (SQLException e) {
			assertEquals(ErrorCode.ValidateVersion.getMessage(), e.getMessage());
		}

		model = getDao().queryByPk(model, new DalHints());
		assertEquals("SH INFO", model.getAddress());
	}
	
	@Test
	public void testVersionIncorrect() throws SQLException {
		SingleUpdateTask<UpdatableVersionModel> test = new SingleUpdateTask<>();
		DalParser<UpdatableVersionModel> parser = new DalDefaultJpaParser<>(UpdatableVersionModel.class, getDbName());
		test.initialize(parser);
		DalHints hints = new DalHints();
		
		ClientTestModel model = getAll().get(0);
		model.setAddress("1122334455");
		Timestamp t = model.getLastChanged();
		t.setTime(t.getTime()+100);
		model.setLastChanged(t);
		
		int result = test.execute(hints, getParser().getFields(model));
		assertIntEquals(0, result);
		model = getDao().queryByPk(model, new DalHints());
		assertEquals("SH INFO", model.getAddress());
	}
	
	@Test
	public void testUpdatableWithVersion() throws SQLException {
		SingleUpdateTask<UpdatableVersionModel> test = new SingleUpdateTask<>();
		DalParser<UpdatableVersionModel> parser = new DalDefaultJpaParser<>(UpdatableVersionModel.class, getDbName());
		test.initialize(parser);
		DalHints hints = new DalHints();
		
		ClientTestModel model = getAll().get(0);
		model.setAddress("1122334455");
		
		int result = test.execute(hints, getParser().getFields(model));
		assertIntEquals(1, result);
		model = getDao().queryByPk(model, new DalHints());
		assertEquals("1122334455", model.getAddress());
	}
	
	@Test
	public void testUpdatableWithVersionByDao() throws SQLException {
		DalParser<UpdatableVersionModel> parser = new DalDefaultJpaParser<>(UpdatableVersionModel.class, getDbName());
		DalTableDao<UpdatableVersionModel> dao = new DalTableDao<>(parser);
		DalHints hints = new DalHints();
		
		UpdatableVersionModel model = dao.query("1=1", new StatementParameters(), new DalHints()).get(0);
		model.setAddress("1122334455");
		model.getLastChanged().setTime(model.getLastChanged().getTime()+100);
		
		int result = dao.update(hints, model);
		assertIntEquals(0, result);
		model = dao.queryByPk(model, new DalHints());
		assertEquals("SH INFO", model.getAddress());
		
		model = dao.query("1=1", new StatementParameters(), new DalHints()).get(0);
		model.setAddress("1122334455");
		result = dao.update(hints, model);
		assertIntEquals(1, result);
		
		model = dao.queryByPk(model, new DalHints());
		assertEquals("1122334455", model.getAddress());
	}
	
	@Test
	public void testNotUpdatableVersion() throws SQLException {
		SingleUpdateTask<NonUpdatableVersionModel> test = new SingleUpdateTask<>();
		DalParser<NonUpdatableVersionModel> parser = new DalDefaultJpaParser<>(NonUpdatableVersionModel.class, getDbName());
		test.initialize(parser);
		DalHints hints = new DalHints();
		
		ClientTestModel model = getAll().get(0);
		model.setAddress("1122334455");
		
		int result = test.execute(hints, getParser().getFields(model));
		assertIntEquals(1, result);
		model = getDao().queryByPk(model, new DalHints());
		assertEquals("1122334455", model.getAddress());
	}
		
	@Test
	public void testNotUpdatableField() throws SQLException {
		//Table Index and Address is not updatable
		SingleUpdateTask<NonUpdatableModel> test = new SingleUpdateTask<>();
		DalParser<NonUpdatableModel> parser = new DalDefaultJpaParser<>(NonUpdatableModel.class, getDbName());
		test.initialize(parser);
		DalHints hints = new DalHints();
		
		ClientTestModel model = getAll().get(0);
		String oldAddr = model.getAddress();
		Integer oldTableIndex = model.getTableIndex();
		
		model.setDbIndex(-100);
		model.setAddress("1122334455");
		model.setTableIndex(100);
		
		int result = test.execute(hints, getParser().getFields(model));
		assertIntEquals(1, result);
		model = getDao().queryByPk(model, new DalHints());
		assertEquals(oldAddr, model.getAddress());
		assertEquals(oldTableIndex, model.getTableIndex());
		assertEquals(-100, model.getDbIndex().intValue());
	}
		
	@Test
	public void testIncludeColumns() throws SQLException {
		//Table Index and Address is not updatable
		SingleUpdateTask<NonUpdatableModel> test = new SingleUpdateTask<>();
		DalParser<NonUpdatableModel> parser = new DalDefaultJpaParser<>(NonUpdatableModel.class, getDbName());
		test.initialize(parser);
		DalHints hints = new DalHints();
		
		ClientTestModel model = getAll().get(0);
		String oldAddr = model.getAddress();
		Integer oldTableIndex = model.getTableIndex();
		Integer oldQuantity= model.getQuantity();
		
		model.setDbIndex(-100);
		model.setAddress("1122334455");
		model.setTableIndex(100);
		model.setQuantity(500);
		model.setType((short)8);
		
		int result = test.execute(hints.include("dbIndex", "type"), getParser().getFields(model));
		assertIntEquals(1, result);
		model = getDao().queryByPk(model, new DalHints());
		assertEquals(oldAddr, model.getAddress());
		assertEquals(oldTableIndex, model.getTableIndex());
		assertEquals(oldQuantity, model.getQuantity());
		assertEquals(-100, model.getDbIndex().intValue());
		assertEquals(8, model.getType().shortValue());
	}
		
	@Test
	public void testIncludeColumnsByDao() throws SQLException {
		//Table Index and Address is not updatable
		DalParser<NonUpdatableModel> parser = new DalDefaultJpaParser<>(NonUpdatableModel.class, getDbName());
		DalHints hints = new DalHints();
		DalTableDao<NonUpdatableModel> dao = new DalTableDao<>(parser);
		
		NonUpdatableModel model = dao.query("1=1", new StatementParameters(), new DalHints()).get(0);
		String oldAddr = model.getAddress();
		Integer oldTableIndex = model.getTableIndex();
		Integer oldQuantity= model.getQuantity();
		
		model.setDbIndex(-100);
		model.setAddress("1122334455");
		model.setTableIndex(100);
		model.setQuantity(500);
		model.setType((short)8);
		
		int result = dao.update(hints.include("dbIndex", "type"), model);
		assertIntEquals(1, result);
		
		model = dao.queryByPk(model, new DalHints());
		assertEquals(oldAddr, model.getAddress());
		assertEquals(oldTableIndex, model.getTableIndex());
		assertEquals(oldQuantity, model.getQuantity());
		assertEquals(-100, model.getDbIndex().intValue());
		assertEquals(8, model.getType().shortValue());
	}
		
	@Test
	public void testExcludeColumns() throws SQLException {
		//Table Index and Address is not updatable
		SingleUpdateTask<NonUpdatableModel> test = new SingleUpdateTask<>();
		DalParser<NonUpdatableModel> parser = new DalDefaultJpaParser<>(NonUpdatableModel.class, getDbName());
		test.initialize(parser);
		DalHints hints = new DalHints();
		
		ClientTestModel model = getAll().get(0);
		String oldAddr = model.getAddress();
		Integer oldTableIndex = model.getTableIndex();
		Integer oldQuantity= model.getQuantity();
		
		model.setDbIndex(-100);
		model.setAddress("1122334455");
		model.setTableIndex(100);
		model.setQuantity(500);
		model.setType((short)8);
		
		int result = test.execute(hints.exclude("quantity"), getParser().getFields(model));
		assertIntEquals(1, result);
		model = getDao().queryByPk(model, new DalHints());
		assertEquals(oldAddr, model.getAddress());
		assertEquals(oldTableIndex, model.getTableIndex());
		assertEquals(oldQuantity, model.getQuantity());
		assertEquals(-100, model.getDbIndex().intValue());
		assertEquals(8, model.getType().shortValue());
	}
		
	@Test
	public void testIncludeExcludeColumns() throws SQLException {
		//Table Index and Address is not updatable
		SingleUpdateTask<NonUpdatableModel> test = new SingleUpdateTask<>();
		DalParser<NonUpdatableModel> parser = new DalDefaultJpaParser<>(NonUpdatableModel.class, getDbName());
		test.initialize(parser);
		DalHints hints = new DalHints();
		
		ClientTestModel model = getAll().get(0);
		String oldAddr = model.getAddress();
		Integer oldTableIndex = model.getTableIndex();
		Integer oldQuantity= model.getQuantity();
		
		model.setDbIndex(-100);
		model.setAddress("1122334455");
		model.setTableIndex(100);
		model.setQuantity(500);
		model.setType((short)8);
		
		int result = test.execute(hints.include("dbIndex", "type", "quantity").exclude("quantity"), getParser().getFields(model));
		assertIntEquals(1, result);
		model = getDao().queryByPk(model, new DalHints());
		assertEquals(oldAddr, model.getAddress());
		assertEquals(oldTableIndex, model.getTableIndex());
		assertEquals(oldQuantity, model.getQuantity());
		assertEquals(-100, model.getDbIndex().intValue());
		assertEquals(8, model.getType().shortValue());
	}
		
	@Entity
	@Database(name="MySqlSimpleDbTableShard")
	@Table(name="dal_client_test")
	public static class UpdatableVersionModel implements DalPojo {
		@Id
		@Column(name="id")
		@Type(value=Types.INTEGER)
		private Integer id;
		
		@Column(name="quantity")
		@Type(value=Types.INTEGER)
		private Integer quantity;
		
		@Column(name="dbIndex")
		@Type(value=Types.INTEGER)
		private Integer dbIndex;
		
		@Column(name="tableIndex")
		@Type(value=Types.INTEGER)
		private Integer tableIndex;
		
		@Column(name="type")
		@Type(value=Types.SMALLINT)
		private Short type;
		
		@Column(name="address")
		@Type(value=Types.VARCHAR)
		private String address;
		
		@Column(name="last_changed")
		@Type(value=Types.TIMESTAMP)
		@Version
		private Timestamp lastChanged;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public Integer getDbIndex() {
			return dbIndex;
		}

		public void setDbIndex(Integer dbIndex) {
			this.dbIndex = dbIndex;
		}

		public Integer getTableIndex() {
			return tableIndex;
		}

		public void setTableIndex(Integer tableIndex) {
			this.tableIndex = tableIndex;
		}
		
		public Short getType() {
			return type;
		}

		public void setType(Short type) {
			this.type = type;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public Timestamp getLastChanged() {
			return lastChanged;
		}

		public void setLastChanged(Timestamp lastChanged) {
			this.lastChanged = lastChanged;
		}
	}
	
	@Entity
	@Database(name="MySqlSimpleDbTableShard")
	@Table(name="dal_client_test")
	public static class NonUpdatableVersionModel implements DalPojo {
		@Id
		@Column(name="id")
		@Type(value=Types.INTEGER)
		private Integer id;
		
		@Column(name="quantity")
		@Type(value=Types.INTEGER)
		private Integer quantity;
		
		@Column(name="dbIndex")
		@Type(value=Types.INTEGER)
		private Integer dbIndex;
		
		@Column(name="tableIndex")
		@Type(value=Types.INTEGER)
		private Integer tableIndex;
		
		@Column(name="type")
		@Type(value=Types.SMALLINT)
		private Short type;
		
		@Column(name="address")
		@Type(value=Types.VARCHAR)
		private String address;
		
		@Column(name="last_changed", updatable=false)
		@Type(value=Types.TIMESTAMP)
		@Version
		private Timestamp lastChanged;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public Integer getDbIndex() {
			return dbIndex;
		}

		public void setDbIndex(Integer dbIndex) {
			this.dbIndex = dbIndex;
		}

		public Integer getTableIndex() {
			return tableIndex;
		}

		public void setTableIndex(Integer tableIndex) {
			this.tableIndex = tableIndex;
		}
		
		public Short getType() {
			return type;
		}

		public void setType(Short type) {
			this.type = type;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public Timestamp getLastChanged() {
			return lastChanged;
		}

		public void setLastChanged(Timestamp lastChanged) {
			this.lastChanged = lastChanged;
		}
	}
	
	@Entity
	@Database(name="MySqlSimpleDbTableShard")
	@Table(name="dal_client_test")
	public static class NonUpdatableModel implements DalPojo {
		@Id
		@Column(name="id")
		@Type(value=Types.INTEGER)
		private Integer id;
		
		@Column(name="quantity")
		@Type(value=Types.INTEGER)
		private Integer quantity;
		
		@Column(name="dbIndex")
		@Type(value=Types.INTEGER)
		private Integer dbIndex;
		
		@Column(name="tableIndex", updatable=false)
		@Type(value=Types.INTEGER)
		private Integer tableIndex;
		
		@Column(name="type")
		@Type(value=Types.SMALLINT)
		private Short type;
		
		@Column(name="address", updatable=false)
		@Type(value=Types.VARCHAR)
		private String address;
		
		@Column(name="last_changed")
		@Type(value=Types.TIMESTAMP)
		private Timestamp lastChanged;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public Integer getDbIndex() {
			return dbIndex;
		}

		public void setDbIndex(Integer dbIndex) {
			this.dbIndex = dbIndex;
		}

		public Integer getTableIndex() {
			return tableIndex;
		}

		public void setTableIndex(Integer tableIndex) {
			this.tableIndex = tableIndex;
		}
		
		public Short getType() {
			return type;
		}

		public void setType(Short type) {
			this.type = type;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public Timestamp getLastChanged() {
			return lastChanged;
		}

		public void setLastChanged(Timestamp lastChanged) {
			this.lastChanged = lastChanged;
		}
	}
}