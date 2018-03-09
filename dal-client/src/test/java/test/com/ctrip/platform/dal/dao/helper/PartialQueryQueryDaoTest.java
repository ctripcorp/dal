package test.com.ctrip.platform.dal.dao.helper;

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
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

/**
 * JUnit test of FreePersonDaoDao class.
 **/
public class PartialQueryQueryDaoTest {
	private static final DatabaseCategory dbCategory = DatabaseCategory.MySql;

	private static final String DATA_BASE = "MySqlSimpleShardForDB";
	// ShardColModShardStrategy;columns=CountryID;mod=2;tableColumns=CityID;tableMod=4;separator=_;shardedTables=person

	private static DalTableDao<Person> pdao;
    private final static String DATABASE_NAME_MYSQL = DATA_BASE;
    private final static String TABLE_NAME = "person";
    private final static int mod = 2;
    
    //Drop the the table
    private final static String DROP_TABLE_SQL_MYSQL_TPL_ORIGINAL = "DROP TABLE IF EXISTS " + TABLE_NAME;
    
    //Create the the table
    // Note that id is UNSIGNED int, which maps to Long in java when using rs.getObject()
    private final static String CREATE_TABLE_SQL_MYSQL_TPL = "CREATE TABLE " + TABLE_NAME +"("
            + "PeopleID int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
            + "Name VARCHAR(64),"
            + "CityID int,"
            + "ProvinceID int,"
            + "CountryID int, "
            + "DataChange_LastTime timestamp default CURRENT_TIMESTAMP)";
    
    private static DalClient clientMySql;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
        clientMySql = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
        DalHints hints = new DalHints();
        String[] sqls = null;
        // For SQL server
        hints = new DalHints();
        for(int i = 0; i < mod; i++) {
            sqls = new String[] { 
                    DROP_TABLE_SQL_MYSQL_TPL_ORIGINAL, 
                    CREATE_TABLE_SQL_MYSQL_TPL};
            clientMySql.batchUpdate(sqls, hints.inShard(i));
        }
        
        DalParser<Person> parser = new DalDefaultJpaParser<>(Person.class, DATA_BASE, TABLE_NAME);
        pdao = new DalTableDao<>(parser);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DalHints hints = new DalHints();
        String[] sqls = null;
        //For Sql Server
        hints = new DalHints();
        for(int i = 0; i < mod; i++) {
            clientMySql.update(DROP_TABLE_SQL_MYSQL_TPL_ORIGINAL, new StatementParameters(), hints.inShard(i));
        }
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

	//Result set is same than entity
	public FreeEntityPojo findFreeFirstSame(String name, List<Integer> cityIds,
			DalHints hints) throws SQLException {
		DalQueryDao queryDao = new DalQueryDao(DATA_BASE);
		DalDefaultJpaMapper<FreeEntityPojo> freeEntityPojoRowMapper = new DalDefaultJpaMapper<>(
				FreeEntityPojo.class);

		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<FreeEntityPojo> builder = new FreeSelectSqlBuilder<>(
				dbCategory);
		builder.setTemplate("SELECT * FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER,
				cityIds);
		builder.mapWith(freeEntityPojoRowMapper).requireFirst().nullable();

		return (FreeEntityPojo) queryDao.query(builder, parameters, hints);
	}

	// Result set is bigger than entity
	public FreeEntityPartialPojo findFreeFirstBigger(String name,
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

		return (FreeEntityPartialPojo) queryDao.query(builder, parameters, hints);
	}

    // Result set is smaller than entity 
    public FreeEntityPartialPojo findFreeFirstSmaller(String name,
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

        return (FreeEntityPartialPojo) queryDao.query(builder, parameters, hints.partialQuery("PeopleID", "Name", "CityID", "ProvinceID"));
    }

    // Result set is not same with entity 
    public FreeEntityMismatchPojo findFreeFirstMismatch(String name,
            List<Integer> cityIds, DalHints hints) throws SQLException {
        DalQueryDao queryDao = new DalQueryDao(DATA_BASE);
        DalDefaultJpaMapper<FreeEntityMismatchPojo> freeEntityPojoRowMapper = new DalDefaultJpaMapper<>(
                FreeEntityMismatchPojo.class);

        hints = DalHints.createIfAbsent(hints);

        FreeSelectSqlBuilder<FreeEntityMismatchPojo> builder = new FreeSelectSqlBuilder<>(
                dbCategory);
        builder.setTemplate("SELECT * FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.setSensitive(i++, "name", Types.VARCHAR, name);
        i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER,
                cityIds);
        builder.mapWith(freeEntityPojoRowMapper).requireFirst().nullable();

        return (FreeEntityMismatchPojo) queryDao.query(builder, parameters, hints);
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
		ret = findFreeFirstSame(name, cityIds, hints.inShard(1).partialQuery("PeopleID", "Name", "CityID"));
		assertNotNull(ret);

		hints = new DalHints();
		ret = findFreeFirstSame(name, cityIds, hints.inAllShards().partialQuery("PeopleID", "Name", "CityID"));
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
            ret = findFreeFirstBigger(name, cityIds, hints.inShard(1).partialQuery("PeopleID", "Name", "CityID", "ProvinceID"));
            fail();
        } catch (DalException e) {
            Assert.assertEquals(ErrorCode.FieldNotExists.getCode(), e.getErrorCode());
        }

        hints = new DalHints().ignoreMissingFields().partialQuery("PeopleID", "Name", "CityID", "ProvinceID");
        ret = findFreeFirstBigger(name, cityIds, hints.inShard(1));
        assertNotNull(ret);

        hints = new DalHints().ignoreMissingFields().partialQuery("PeopleID", "Name", "CityID", "ProvinceID");
        ret = findFreeFirstBigger(name, cityIds, hints.inAllShards());
        assertNotNull(ret);
    }

    @Test
    public void testAllowPartial() throws Exception {
        String name = "Test";// Test value here
        List<Integer> cityIds = new ArrayList<>();
        cityIds.add(1);
        cityIds.add(2);
        cityIds.add(3);
        DalHints hints = new DalHints();

        FreeEntityMismatchPojo ret;
        try {
            hints = new DalHints();
            ret = findFreeFirstMismatch(name, cityIds, hints.inShard(1));
            fail();
        } catch (DalException e) {
            Assert.assertEquals(ErrorCode.ResultMappingError.getCode(), e.getErrorCode());
        }

        hints = new DalHints().allowPartial();
        ret = findFreeFirstMismatch(name, cityIds, hints.inShard(1));
        assertNotNull(ret);

        hints = new DalHints().allowPartial();
        ret = findFreeFirstMismatch(name, cityIds, hints.inAllShards());
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
	
    @Entity
    @Database(name = "")
    @Table(name = "")
    public static class FreeEntityMismatchPojo implements DalPojo {

        @Column(name = "PeopleID")
        @Type(value = Types.INTEGER)
        private Integer peopleID;

        @Column(name = "Name")
        @Type(value = Types.VARCHAR)
        private String name;

        @Column(name = "CityID")
        @Type(value = Types.INTEGER)
        private Integer cityID;

        @Column(name = "Province")
        @Type(value = Types.INTEGER)
        private Integer provinceID;

        @Column(name = "Country")
        @Type(value = Types.INTEGER)
        private Integer countryID;

        @Column(name = "Time")
        @Type(value = Types.INTEGER)
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
