package intest;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import microsoft.sql.DateTimeOffset;

import com.ctrip.fx.octopus.util.Time;
import com.ctrip.platform.dal.dao.*;

public class TestTableDaoTest {
	public static void main(String[] args) {
		
		try {
			/**
			* Initialize DalClientFactory.
			* The Dal.config can be specified from class-path or local file path.
			* One of follow three need to be enabled.
			**/
			//DalClientFactory.initPrivateFactory(); //Load from class-path connections.properties
			DalClientFactory.initClientFactory(); // load from class-path Dal.config
			//DalClientFactory.initClientFactory("E:/DalMult.config"); // load from the specified Dal.config file path
			
			TestTableDao dao = new TestTableDao();
		
			TestTable model = new TestTable();
			model.setBigint(Long.valueOf(10));
			model.setBinary("This is a binary".getBytes());
			model.setBit(true);
			model.setChar("C");
			model.setCharone("C");
			model.setDate(Date.valueOf("2014-6-19"));
			model.setDatetime(new Timestamp(System.currentTimeMillis()));
			model.setDatetime2(new Timestamp(System.currentTimeMillis()));
			model.setDatetimeoffset(DateTimeOffset.valueOf(model.getDatetime(), 1));
			model.setDecimal(BigDecimal.ONE);
			model.setFloat(Double.valueOf(100));
			model.setGuid("2A66057D-F4E5-4E2B-B2F1-38C51A96D385");
			//model.setID(1);
			model.setImage("This is an image".getBytes());
			model.setMoney(BigDecimal.ZERO);
			model.setNchar("T");
			model.setNtext("This is ntext");
			model.setNumeric(BigDecimal.TEN);
			model.setNvarchar("C");
			model.setReal(Float.MAX_VALUE);
			model.setSmalldatetime(new Timestamp(System.currentTimeMillis()));
			model.setSmallint(Short.valueOf("1"));
			model.setSmallmoney(BigDecimal.ONE);
			model.setText("This is text");
			model.setText(Time.toString(System.currentTimeMillis()));
			model.setTimestamp(null);
			model.setTinyint(Short.valueOf("16"));
			model.setVarchar("V");
			model.setXml("<xml>hello</xml>");
			
			dao.insert(model);
			
			List<TestTable> ts = dao.queryByPage(100, 1);
			
			System.out.println(ts.size());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		System.exit(1);
	}

}
