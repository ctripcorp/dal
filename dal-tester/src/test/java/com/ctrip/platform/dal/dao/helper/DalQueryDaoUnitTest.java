package com.ctrip.platform.dal.dao.helper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

/**
 * JUnit test of FreePersonDaoDao class.
 **/
public class DalQueryDaoUnitTest {
	private static final DatabaseCategory dbCategory = DatabaseCategory.MySql;
	private static final String DATA_BASE = "MySqlSimpleShard";
	// ShardColModShardStrategy;columns=CountryID;mod=2;tableColumns=CityID;tableMod=4;separator=_;shardedTables=person

	private static DalTableDao<Person> pdao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/**
		 * Initialize DalClientFactory. The Dal.config can be specified from
		 * class-path or local file path. One of follow three need to be
		 * enabled.
		 **/
		// DalClientFactory.initPrivateFactory(); //Load from class-path
		// connections.properties
		DalClientFactory.initClientFactory(); // load from class-path Dal.config
		// DalClientFactory.initClientFactory("E:/DalMult.config"); // load from
		// the specified Dal.config file path

		DalParser<Person> parser = new DalDefaultJpaParser<>(Person.class,
				"MySqlSimpleShard", "PERSON");
		pdao = new DalTableDao<>(parser);
	}

	@Before
	public void setUp() throws Exception {
		tearDown();
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				try {
					pdao.insert(new DalHints().enableIdentityInsert(),
							createPojo(i, j, j + 1));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Person createPojo(int countryID, int cityID, int id) {
		Person daoPojo;

		daoPojo = new Person();
		daoPojo.setCountryID(countryID);
		daoPojo.setCityID(cityID);
		daoPojo.setName("Test");
		daoPojo.setPeopleID(id);

		return daoPojo;
	}

	@After
	public void tearDown() throws Exception {
		for (int i = 0; i < 2; i++) {
			pdao.delete(
					new DalHints().inShard(i),
					pdao.query("1=1", new StatementParameters(),
							new DalHints().inShard(i)));
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	public FreeEntityPojo findFreeFirstPartial(String name, List<Integer> cityIds,
			DalHints hints) throws SQLException {
		DalQueryDao queryDao = new DalQueryDao(DATA_BASE);
		DalDefaultJpaMapper<FreeEntityPojo> freeEntityPojoRowMapper = new DalDefaultJpaMapper<>(
				FreeEntityPojo.class);

		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<FreeEntityPojo> builder = new FreeSelectSqlBuilder<>(
				dbCategory);
		builder.setTemplate("SELECT PeopleID, Name, CityID, ProvinceID, CountryID, DataChange_LastTime FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER,
				cityIds);
		builder.mapWith(freeEntityPojoRowMapper).requireFirst().nullable();

		return (FreeEntityPojo) queryDao.query(builder, parameters,
				hints.partialQuery("PeopleID", "Name", "CityID"));
	}

	public FreeEntityPartialPojo findFreeFirstMissingFields(String name,
			List<Integer> cityIds, DalHints hints) throws SQLException {
		DalQueryDao queryDao = new DalQueryDao(DATA_BASE);
		DalDefaultJpaMapper<FreeEntityPartialPojo> freeEntityPojoRowMapper = new DalDefaultJpaMapper<>(
				FreeEntityPartialPojo.class);

		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<FreeEntityPartialPojo> builder = new FreeSelectSqlBuilder<>(
				dbCategory);
		builder.setTemplate("SELECT * FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER,
				cityIds);
		builder.mapWith(freeEntityPojoRowMapper).requireFirst().nullable();

		return (FreeEntityPartialPojo) queryDao.query(builder, parameters, hints.partialQuery("PeopleID", "Name", "", "CityID", "ProvinceID"));
	}

	@Test
	public void testSelectPartial() throws Exception {
		String name = "Test";// Test value here
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);
		DalHints hints = new DalHints();

		FreeEntityPojo ret;

		hints = new DalHints();
		ret = findFreeFirstPartial(name, cityIds, hints.inShard(1));
		assertNotNull(ret);

		hints = new DalHints();
		ret = findFreeFirstPartial(name, cityIds, hints.inAllShards());
		assertNotNull(ret);
	}

	@Test
	public void testIgnorMissingFields() throws Exception {
		String name = "Test";// Test value here
		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);
		DalHints hints = new DalHints();

		FreeEntityPartialPojo ret;
		try {
			hints = new DalHints();
			ret = findFreeFirstMissingFields(name, cityIds, hints.inShard(1));
			fail();
		} catch (DalException e) {
			Assert.assertEquals(ErrorCode.FieldNotExists.getCode(), e.getErrorCode());
		}

		hints = new DalHints().ignorMissingFields();
		ret = findFreeFirstMissingFields(name, cityIds, hints.inShard(1));
		assertNotNull(ret);

		hints = new DalHints().ignorMissingFields();
		ret = findFreeFirstMissingFields(name, cityIds, hints.inAllShards());
		assertNotNull(ret);
	}

	@Entity
	@Database(name = "")
	@Table(name = "")
	public static class FreeEntityPartialPojo implements DalPojo {

		@Column(name = "PeopleID")
		@Type(value = Types.INTEGER)
		private Integer peopleID;

		@Column(name = "Name")
		@Type(value = Types.VARCHAR)
		private String name;

		@Column(name = "CityID")
		@Type(value = Types.INTEGER)
		private Integer cityID;

		private Integer provinceID;

		private Integer countryID;

		private Timestamp dataChange_LastTime;

		public Integer getPeopleID() {
			return peopleID;
		}

		public void setPeopleID(Integer peopleID) {
			this.peopleID = peopleID;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getCityID() {
			return cityID;
		}

		public void setCityID(Integer cityID) {
			this.cityID = cityID;
		}

		public Integer getProvinceID() {
			return provinceID;
		}

		public void setProvinceID(Integer provinceID) {
			this.provinceID = provinceID;
		}

		public Integer getCountryID() {
			return countryID;
		}

		public void setCountryID(Integer countryID) {
			this.countryID = countryID;
		}

		public Timestamp getDataChange_LastTime() {
			return dataChange_LastTime;
		}

		public void setDataChange_LastTime(Timestamp dataChange_LastTime) {
			this.dataChange_LastTime = dataChange_LastTime;
		}

	}
}
