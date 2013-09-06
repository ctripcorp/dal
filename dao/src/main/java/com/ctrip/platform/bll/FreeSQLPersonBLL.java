package com.ctrip.platform.bll;

import java.sql.ResultSet;

import org.msgpack.type.Value;
import org.msgpack.type.IntegerValue;
import org.msgpack.type.ValueFactory;

import com.ctrip.platform.dao.FreeSQLPersonDAO;
import com.ctrip.platform.dao.msg.AvailableType;

public class FreeSQLPersonBLL {
	
	public static void main(String[] args) throws Exception {
		
//		Object a = new int[]{1,2};
//		
//		System.out.println(a.getClass().isArray());
		
		Value v = ValueFactory.createIntegerValue(1);
		
		System.out.println(v.asIntegerValue().getInt());
		
//		try {
//			FreeSQLPersonDAO person = new FreeSQLPersonDAO();
//
//			person.setUseDBClient(false);
//
//			AvailableType nameParam = new AvailableType(1, "1");
//			AvailableType genderParam = new AvailableType(2, new int[]{1,2});
//
//			ResultSet rs = person.getAddrAndTel(genderParam,
//					nameParam);
//			
//			while (rs.next()) {
//				System.out.println(rs.getString(1));
//				System.out.println(rs.getString(2));
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
