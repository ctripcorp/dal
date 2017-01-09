package com.ctrip.platform.dal.dao.helper;

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

import com.ctrip.platform.dal.codegen.Person;
import com.ctrip.platform.dal.codegen.PersonDao;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

/**
 * JUnit test of PersonDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
**/
public class TableDaoUnitTest {

	private static final String DATA_BASE = "MySqlSimpleShard";
	//ShardColModShardStrategy;columns=CountryID;mod=2;tableColumns=CityID;tableMod=4;separator=_;shardedTables=person

	private static PersonDao dao = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/**
		* Initialize DalClientFactory.
		* The Dal.config can be specified from class-path or local file path.
		* One of follow three need to be enabled.
		**/
//		DalClientFactory.initClientFactory(); // load from class-path Dal.config
//		DalClientFactory.warmUpConnections();
		dao = new PersonDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
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
				dao.delete(new DalHints().inShard(i).inTableShard(j), dao.queryAll(new DalHints().inShard(i).inTableShard(j)));
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
	public void testFindByPartial() throws Exception {
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
