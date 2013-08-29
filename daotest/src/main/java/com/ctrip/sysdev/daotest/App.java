package com.ctrip.sysdev.daotest;

import java.sql.ResultSet;

import com.ctrip.sysdev.apptools.daogen.dao.PersonDAO;
import com.ctrip.sysdev.apptools.daogen.dao.msg.AvailableType;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        PersonDAO person = new PersonDAO();
        try {
        	AvailableType at = new <Integer> AvailableType(1, 4);
        	person.setUseDBClient(true);
			ResultSet rs= person.getByAll(at);
			while(rs.next()){
				System.out.println(rs.getInt(1));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
