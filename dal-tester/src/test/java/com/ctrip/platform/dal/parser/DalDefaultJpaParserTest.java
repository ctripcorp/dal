package com.ctrip.platform.dal.parser;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.helper.Type;

public class DalDefaultJpaParserTest {
	
	private static DalParser<PersonEntity> personParser = null;
	private static DalParser<AllTypesEntity> allTypesParser = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalClientFactory.initClientFactory();
		personParser = DalDefaultJpaParser.create(PersonEntity.class, "dao_test_M");
		allTypesParser = DalDefaultJpaParser.create(AllTypesEntity.class, "dao_test_S");
				
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Assert.assertEquals("dao_test_M", personParser.getDatabaseName());
		Assert.assertEquals("person", personParser.getTableName());
		Assert.assertEquals("[id, name, age]", Arrays.toString(personParser.getColumnNames()));
		Assert.assertEquals("[4, 12, 4]", Arrays.toString(personParser.getColumnTypes()));
		Assert.assertEquals("[id]", Arrays.toString(personParser.getPrimaryKeyNames()));
		PersonEntity person = new PersonEntity();
		person.setId(123456);
		Assert.assertNotNull(personParser.getFields(person));
		Assert.assertNotNull(personParser.getPrimaryKeys(person));
		Assert.assertTrue(personParser.getIdentityValue(person).intValue() == 123456);

		Assert.assertEquals("dao_test_S", allTypesParser.getDatabaseName());
		Assert.assertEquals("All_Types", allTypesParser.getTableName());
		Assert.assertEquals("[iDCol, intCol, bigIntCol, decimalcol, doubleCol, "
				+ "floatCol, mediumIntCol, smallIntCol, tinyIntCol, charCol, varCharCol, "
				+ "binaryCol, blobCol, longBlobCol, mediumBlobCol, tinyBlobCol, varBinaryCol, "
				+ "dateCol, dateTimeCol, timeCol, timeStampCol, yearCol, geometryCol, geometryCollectionCol, "
				+ "longTextCol, mediumTextCol, textCol, tinyTextCol, bitCol, enumCol, setCol]", 
				Arrays.toString(allTypesParser.getColumnNames()));
		Assert.assertEquals("[4, 4, -5, 3, 8, 7, 4, 5, -6, 1, 12, -2, -4, -4, -4, -3, -3, 91, 93, 92, 93, 91, "
				+ "-2, -2, -1, -1, -1, -1, -7, 1, 1]", Arrays.toString(allTypesParser.getColumnTypes()));
		Assert.assertEquals("[iDCol]", Arrays.toString(allTypesParser.getPrimaryKeyNames()));
		AllTypesEntity allTypes = new AllTypesEntity();
		allTypes.setIDCol(Long.MAX_VALUE);
		Assert.assertNotNull(allTypesParser.getFields(allTypes));
		Assert.assertNotNull(allTypesParser.getPrimaryKeys(allTypes));
		Assert.assertTrue(allTypesParser.getIdentityValue(allTypes).longValue() == Long.MAX_VALUE);
	}
	
	@Entity
	@Table(name="person")
	static class PersonEntity implements DalPojo {
		
		@Id
		@GeneratedValue(strategy=GenerationType.AUTO)
		@Type(value=Types.INTEGER)
		private int id;
		
		@Column(name="name")
		@Type(value=Types.VARCHAR)
		private String name;
		
		@Column(name="age")
		@Type(value=Types.INTEGER)
		private Integer age;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

	}
	
	@Entity
	@Table(name="All_Types")
	static class AllTypesEntity implements DalPojo {
		
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		@Type(value=Types.INTEGER)
		private Long iDCol;
		
		@Column(name="intCol")
		@Type(value=Types.INTEGER)
		private Integer intCol;
		
		@Column(name="bigIntCol")
		@Type(value=Types.BIGINT)
		private Long bigIntCol;
		
		@Column(name="decimalcol")
		@Type(value=Types.DECIMAL)
		private BigDecimal decimalcol;
		
		@Column(name="doubleCol")
		@Type(value=Types.DOUBLE)
		private Double doubleCol;
		
		@Column(name="floatCol")
		@Type(value=Types.REAL)
		private Float floatCol;
		
		@Column(name="mediumIntCol")
		@Type(value=Types.INTEGER)
		private Integer mediumIntCol;
		
		@Column(name="smallIntCol")
		@Type(value=Types.SMALLINT)
		private Integer smallIntCol;
		
		@Column(name="tinyIntCol")
		@Type(value=Types.TINYINT)
		private Integer tinyIntCol;
		
		@Column(name="charCol")
		@Type(value=Types.CHAR)
		private String charCol;
		
		@Column(name="varCharCol")
		@Type(value=Types.VARCHAR)
		private String varCharCol;
		
		@Column(name="binaryCol")
		@Type(value=Types.BINARY)
		private byte[] binaryCol;
		
		@Column(name="blobCol")
		@Type(value=Types.LONGVARBINARY)
		private byte[] blobCol;
		
		@Column(name="longBlobCol")
		@Type(value=Types.LONGVARBINARY)
		private byte[] longBlobCol;
		
		@Column(name="mediumBlobCol")
		@Type(value=Types.LONGVARBINARY)
		private byte[] mediumBlobCol;
		
		@Column(name="tinyBlobCol")
		@Type(value=Types.VARBINARY)
		private byte[] tinyBlobCol;
		
		@Column(name="varBinaryCol")
		@Type(value=Types.VARBINARY)
		private byte[] varBinaryCol;
		
		@Column(name="dateCol")
		@Type(value=Types.DATE)
		private Date dateCol;
		
		@Column(name="dateTimeCol")
		@Type(value=Types.TIMESTAMP)
		private Timestamp dateTimeCol;
		
		@Column(name="timeCol")
		@Type(value=Types.TIME)
		private Time timeCol;
		
		@Column(name="timeStampCol")
		@Type(value=Types.TIMESTAMP)
		private Timestamp timeStampCol;
		
		@Column(name="yearCol")
		@Type(value=Types.DATE)
		private Date yearCol;
		
		@Column(name="geometryCol")
		@Type(value=Types.BINARY)
		private byte[] geometryCol;
		
		@Column(name="geometryCollectionCol")
		@Type(value=Types.BINARY)
		private byte[] geometryCollectionCol;
		
		@Column(name="longTextCol")
		@Type(value=Types.LONGVARCHAR)
		private String longTextCol;
		
		@Column(name="mediumTextCol")
		@Type(value=Types.LONGVARCHAR)
		private String mediumTextCol;
		
		@Column(name="textCol")
		@Type(value=Types.LONGVARCHAR)
		private String textCol;
		
		@Column(name="tinyTextCol")
		@Type(value=Types.LONGVARCHAR)
		private String tinyTextCol;
		
		@Column(name="bitCol")
		@Type(value=Types.BIT)
		private Boolean bitCol;
		
		@Column(name="enumCol")
		@Type(value=Types.CHAR)
		private String enumCol;
		
		@Column(name="setCol")
		@Type(value=Types.CHAR)
		private String setCol;

		public Long getIDCol() {
			return iDCol;
		}

		public void setIDCol(Long iDCol) {
			this.iDCol = iDCol;
		}

		public Integer getIntCol() {
			return intCol;
		}

		public void setIntCol(Integer intCol) {
			this.intCol = intCol;
		}

		public Long getBigIntCol() {
			return bigIntCol;
		}

		public void setBigIntCol(Long bigIntCol) {
			this.bigIntCol = bigIntCol;
		}

		public BigDecimal getDecimalcol() {
			return decimalcol;
		}

		public void setDecimalcol(BigDecimal decimalcol) {
			this.decimalcol = decimalcol;
		}

		public Double getDoubleCol() {
			return doubleCol;
		}

		public void setDoubleCol(Double doubleCol) {
			this.doubleCol = doubleCol;
		}

		public Float getFloatCol() {
			return floatCol;
		}

		public void setFloatCol(Float floatCol) {
			this.floatCol = floatCol;
		}

		public Integer getMediumIntCol() {
			return mediumIntCol;
		}

		public void setMediumIntCol(Integer mediumIntCol) {
			this.mediumIntCol = mediumIntCol;
		}

		public Integer getSmallIntCol() {
			return smallIntCol;
		}

		public void setSmallIntCol(Integer smallIntCol) {
			this.smallIntCol = smallIntCol;
		}

		public Integer getTinyIntCol() {
			return tinyIntCol;
		}

		public void setTinyIntCol(Integer tinyIntCol) {
			this.tinyIntCol = tinyIntCol;
		}

		public String getCharCol() {
			return charCol;
		}

		public void setCharCol(String charCol) {
			this.charCol = charCol;
		}

		public String getVarCharCol() {
			return varCharCol;
		}

		public void setVarCharCol(String varCharCol) {
			this.varCharCol = varCharCol;
		}

		public byte[] getBinaryCol() {
			return binaryCol;
		}

		public void setBinaryCol(byte[] binaryCol) {
			this.binaryCol = binaryCol;
		}

		public byte[] getBlobCol() {
			return blobCol;
		}

		public void setBlobCol(byte[] blobCol) {
			this.blobCol = blobCol;
		}

		public byte[] getLongBlobCol() {
			return longBlobCol;
		}

		public void setLongBlobCol(byte[] longBlobCol) {
			this.longBlobCol = longBlobCol;
		}

		public byte[] getMediumBlobCol() {
			return mediumBlobCol;
		}

		public void setMediumBlobCol(byte[] mediumBlobCol) {
			this.mediumBlobCol = mediumBlobCol;
		}

		public byte[] getTinyBlobCol() {
			return tinyBlobCol;
		}

		public void setTinyBlobCol(byte[] tinyBlobCol) {
			this.tinyBlobCol = tinyBlobCol;
		}

		public byte[] getVarBinaryCol() {
			return varBinaryCol;
		}

		public void setVarBinaryCol(byte[] varBinaryCol) {
			this.varBinaryCol = varBinaryCol;
		}

		public Date getDateCol() {
			return dateCol;
		}

		public void setDateCol(Date dateCol) {
			this.dateCol = dateCol;
		}

		public Timestamp getDateTimeCol() {
			return dateTimeCol;
		}

		public void setDateTimeCol(Timestamp dateTimeCol) {
			this.dateTimeCol = dateTimeCol;
		}

		public Time getTimeCol() {
			return timeCol;
		}

		public void setTimeCol(Time timeCol) {
			this.timeCol = timeCol;
		}

		public Timestamp getTimeStampCol() {
			return timeStampCol;
		}

		public void setTimeStampCol(Timestamp timeStampCol) {
			this.timeStampCol = timeStampCol;
		}

		public Date getYearCol() {
			return yearCol;
		}

		public void setYearCol(Date yearCol) {
			this.yearCol = yearCol;
		}

		public byte[] getGeometryCol() {
			return geometryCol;
		}

		public void setGeometryCol(byte[] geometryCol) {
			this.geometryCol = geometryCol;
		}

		public byte[] getGeometryCollectionCol() {
			return geometryCollectionCol;
		}

		public void setGeometryCollectionCol(byte[] geometryCollectionCol) {
			this.geometryCollectionCol = geometryCollectionCol;
		}

		public String getLongTextCol() {
			return longTextCol;
		}

		public void setLongTextCol(String longTextCol) {
			this.longTextCol = longTextCol;
		}

		public String getMediumTextCol() {
			return mediumTextCol;
		}

		public void setMediumTextCol(String mediumTextCol) {
			this.mediumTextCol = mediumTextCol;
		}

		public String getTextCol() {
			return textCol;
		}

		public void setTextCol(String textCol) {
			this.textCol = textCol;
		}

		public String getTinyTextCol() {
			return tinyTextCol;
		}

		public void setTinyTextCol(String tinyTextCol) {
			this.tinyTextCol = tinyTextCol;
		}

		public Boolean getBitCol() {
			return bitCol;
		}

		public void setBitCol(Boolean bitCol) {
			this.bitCol = bitCol;
		}

		public String getEnumCol() {
			return enumCol;
		}

		public void setEnumCol(String enumCol) {
			this.enumCol = enumCol;
		}

		public String getSetCol() {
			return setCol;
		}

		public void setSetCol(String setCol) {
			this.setCol = setCol;
		}

	}

}
