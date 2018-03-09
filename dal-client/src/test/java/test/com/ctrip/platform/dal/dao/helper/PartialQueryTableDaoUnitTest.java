package test.com.ctrip.platform.dal.dao.helper;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

/**
 * JUnit test of PersonDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class PartialQueryTableDaoUnitTest {

	private static DalTableDao<Person> dao = null;

	private final static String DATABASE_NAME_MYSQL = "MySqlSimpleShard";
    private final static String TABLE_NAME = "person";
    private final static int mod = 2;
    private final static int tableMod = 4;
    
    //Create the the table
    private final static String DROP_TABLE_SQL_MYSQL_TPL_ORIGINAL = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private final static String DROP_TABLE_SQL_MYSQL_TPL = "DROP TABLE IF EXISTS " + TABLE_NAME + "_%d";
    
    //Create the the table
    // Note that id is UNSIGNED int, which maps to Long in java when using rs.getObject()
    private final static String CREATE_TABLE_SQL_MYSQL_TPL = "CREATE TABLE " + TABLE_NAME +"_%d("
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
            clientMySql.update(DROP_TABLE_SQL_MYSQL_TPL_ORIGINAL, new StatementParameters(), hints.inShard(i));
            for(int j = 0; j < tableMod; j++) {
                sqls = new String[] { 
                        String.format(DROP_TABLE_SQL_MYSQL_TPL, j), 
                        String.format(CREATE_TABLE_SQL_MYSQL_TPL, j)};
                clientMySql.batchUpdate(sqls, hints.inShard(i));
            }
        }
        
        dao = new DalTableDao<>(new DalDefaultJpaParser<>(Person.class));
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DalHints hints = new DalHints();
        String[] sqls = null;
        //For Sql Server
        hints = new DalHints();
        for(int i = 0; i < mod; i++) {
            clientMySql.update(DROP_TABLE_SQL_MYSQL_TPL_ORIGINAL, new StatementParameters(), hints.inShard(i));
            sqls = new String[tableMod];
            for(int j = 0; j < tableMod; j++) {
                sqls[j] = String.format(DROP_TABLE_SQL_MYSQL_TPL, j);
            }
            clientMySql.batchUpdate(sqls, hints.inShard(i));
        }
    }
   
	@Before
	public void setUp() throws Exception {
		tearDown();
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 4; j++) {
				List<Person> daoPojo = createPojos(i, j);
	
				try {
					dao.insert(new DalHints().enableIdentityInsert(), daoPojo);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private List<Person> createPojos(int countryID, int cityID) {
		List<Person> pl = new ArrayList<>();
		
		Person daoPojo;

		for (int i = 0; i < 4; i++) {
			daoPojo = new Person();
			daoPojo.setCountryID(countryID);
			daoPojo.setCityID(cityID);
			daoPojo.setName("Test");
			daoPojo.setPeopleID(i+1);
			pl.add(daoPojo);
		}
		
		return pl;
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

	private void changePojo(Person daoPojo) {
		daoPojo.setName(daoPojo.getName() + " changed");
	}
	
	private void changePojos(List<Person> daoPojos) {
		for(Person daoPojo: daoPojos)
			changePojo(daoPojo);
	}
	
	private void verifyPojo(Person daoPojo) {
		assertEquals("Test changed", daoPojo.getName());
	}
	
	private void verifyPojos(List<Person> daoPojos) {
		for(Person daoPojo: daoPojos)
			verifyPojo(daoPojo);
	}
	
	@After
	public void tearDown() throws Exception {
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 4; j++) {
				dao.delete(new DalHints().inShard(i).inTableShard(j), dao.query("1=1", new StatementParameters(), new DalHints().inShard(i).inTableShard(j)));
			}
		}
	}
	
	@Test
	public void testDetectFieldNotExist() throws Exception {
		DalTableDao<PersonWithoutName> client = new DalTableDao<>(new DalDefaultJpaParser<>(PersonWithoutName.class));
		List<Integer> peopleIds = new ArrayList<>();
		peopleIds.add(1);
		peopleIds.add(2);
		peopleIds.add(3);

		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("DataChange_LastTime","CityID","Name","ProvinceID","PeopleID","CountryID");
		builder.in("PeopleID", peopleIds, Types.INTEGER, false);
		builder.and();
		builder.in("CityID", cityIds, Types.INTEGER, false);

		try {
			client.query(builder, new DalHints().inAllShards().inTableShard(1));
			Assert.fail();
		} catch (DalException e) {
			e.printStackTrace();
			assertEquals(ErrorCode.FieldNotExists.getCode(), e.getErrorCode());
		}
	}
	
	@Test
	public void testFindBySelectedField() throws Exception {
		DalTableDao<Person> client = new DalTableDao<>(new DalDefaultJpaParser<>(Person.class));
		List<Integer> peopleIds = new ArrayList<>();
		peopleIds.add(1);
		peopleIds.add(2);
		peopleIds.add(3);

		List<Integer> cityIds = new ArrayList<>();
		cityIds.add(1);
		cityIds.add(2);
		cityIds.add(3);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("DataChange_LastTime","CityID","Name","ProvinceID");
		builder.in("PeopleID", peopleIds, Types.INTEGER, false);
		builder.and();
		builder.in("CityID", cityIds, Types.INTEGER, false);

		try {
			List<Person> ret = client.query(builder, new DalHints().inAllShards().inTableShard(1));
			Assert.assertNull(ret.get(0).getCountryID());
			Assert.assertNull(ret.get(0).getPeopleID());
		} catch (DalException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testFindByPartial() throws Exception {
		DalTableDao<Person> client = new DalTableDao<>(new DalDefaultJpaParser<>(Person.class));
		List<Person> pl;
		
		pl = client.query("1=1", new StatementParameters(), new DalHints().partialQuery("Name","CountryID").inShard(1).inTableShard(1));
		assertPersonList(pl);
		
		pl = client.queryFrom("1=1", new StatementParameters(), new DalHints().partialQuery("Name","CountryID").inAllShards().inTableShard(1), 1, 10);
		assertPersonList(pl);

		Person sample = new Person();
		sample.setCountryID(1);
		pl = client.queryLike(sample, new DalHints().partialQuery("Name","CountryID").inAllShards().inTableShard(1));
		assertPersonList(pl);
		
		pl = client.queryTop("1=1", new StatementParameters(), new DalHints().partialQuery("Name","CountryID").inAllShards().inTableShard(1), 100);
		assertPersonList(pl);
		
		Person test = client.queryByPk(1, new DalHints().partialQuery("Name","CountryID").inShard(1).inTableShard(1));
		assertPerson(test);
		
		test.setPeopleID(1);
		test = client.queryByPk(test, new DalHints().partialQuery("Name","CountryID").inShard(1).inTableShard(1));
		assertPerson(test);
		
		test = client.queryFirst("1=1", new StatementParameters(), new DalHints().partialQuery("Name","CountryID").inShard(1).inTableShard(1));
		assertPerson(test);
	}
	
	private void assertPersonList(List<Person> pl) {
		for(Person p : pl)
			assertPerson(p);
	}
	
	private void assertPerson(Person p) {
		Assert.assertNotNull(p.getName());
		Assert.assertNotNull(p.getCountryID());
		
		Assert.assertNull(p.getProvinceID());
		Assert.assertNull(p.getPeopleID());
		Assert.assertNull(p.getDataChange_LastTime());
		Assert.assertNull(p.getCityID());
	}
	
	@Entity
	@Database(name="MySqlSimpleShard")
	@Table(name="person")
	public static class PersonWithoutName implements DalPojo {
		
		@Id
		@Column(name="PeopleID")
		@GeneratedValue(strategy = GenerationType.AUTO)
		@Type(value=Types.INTEGER)
		private Integer peopleID;
		
		private String name;
		
		@Column(name="CityID")
		@Type(value=Types.INTEGER)
		private Integer cityID;
		
		@Column(name="ProvinceID")
		@Type(value=Types.INTEGER)
		private Integer provinceID;
		
		@Column(name="CountryID")
		@Type(value=Types.INTEGER)
		private Integer countryID;
		
		@Column(name="DataChange_LastTime")
		@Type(value=Types.TIMESTAMP)
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
