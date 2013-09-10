package com.ctrip.sysdev.daotest;

import java.sql.ResultSet;

import com.ctrip.platform.international.daogen.dao.PersonDAO;
import com.ctrip.platform.international.daogen.dao.param.ParameterFactory;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
       PersonDAO person = new PersonDAO();
       person.setUseDBClient(true);
       try {
		ResultSet rs = person.get(ParameterFactory.createIntArrayParameter(1, new int[]{1,2}));
		
		while(rs.next()){
			System.out.println(rs.getString(1));
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
}
