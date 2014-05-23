package com.ctrip.platform.sqlserver.types.test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestTableDaoTest {
	public static void main(String[] args) {
		
		try {
			/**
			* Initialize DalClientFactory.
			* The Dal.config can be specified from class-path or local file path.
			* One of follow three need to be enabled.
			**/
			DalClientFactory.initPrivateFactory(); //Load from class-path connections.properties
			//DalClientFactory.initClientFactory(); // load from class-path Dal.config
			//DalClientFactory.initClientFactory("E:/DalMult.config"); // load from the specified Dal.config file path
			
			TestTableDao dao = new TestTableDao();

		    TestTable pojo = new TestTable();
		    pojo.setDate(new Date(System.currentTimeMillis()));
		    pojo.setDatetime(new Timestamp(System.currentTimeMillis()));
		    pojo.setDatetime2(new Timestamp(System.currentTimeMillis()));
		    pojo.setSmalldatetime(new Timestamp(System.currentTimeMillis()));
		    pojo.setDatetimeoffset(new Timestamp(System.currentTimeMillis()));
		    pojo.setBigint(new Long(123));
		    pojo.setBinary(new byte[]{1,1,1,1,});
		    pojo.setBit(true);
		    pojo.setChar("C");
		    pojo.setCharone("C");
		    pojo.setFloat(new Double(10.01));
		    pojo.setGuid(java.util.UUID.randomUUID().toString());
		    pojo.setImage(new byte[]{0,0,0,1});
		    pojo.setMoney(new BigDecimal("1.00000000000000000"));
		    pojo.setNumeric(new BigDecimal(10000));
		    pojo.setReal(new Float("0.24"));
		    pojo.setSmallint((((Number)(128)).shortValue()));
		    pojo.setNtext("this is a ntext");
		    pojo.setXml("<xml>this is a xml</xml>");
		    pojo.setTime(new Time(System.currentTimeMillis()));
		    pojo.setTinyint((((Number)(64)).shortValue()));
		    pojo.setSmallmoney(new BigDecimal(12));
		    pojo.setText("This a test text");
			pojo.setVarchar("This a test varchar");
			pojo.setNvarchar("This a test nvarchar");
			pojo.setDecimal(new BigDecimal(124.0));
			pojo.setNchar("T");
			
			dao.insert(pojo);
			
			List<TestTable> tables = dao.queryWhereClause("id < 100");
			System.out.println(tables.size());
			ObjectMapper objectMapper = new ObjectMapper();
			JsonGenerator  jsonGenerator = objectMapper.getFactory().createGenerator(System.out, JsonEncoding.UTF8);
			for (TestTable table : tables) {
				 jsonGenerator.writeObject(table);
				 System.out.print(System.lineSeparator());
			}
		    System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
