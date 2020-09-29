package com.ctrip.datasource.titan;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TitanDataSourceLocatorTest {
    @Test
    public void testGetFromTitanServiceSuccess() {
        TitanDataSourceLocator test = new TitanDataSourceLocator();
        String titanSvcUrl = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query/";;
        String name = "fxdalclusterbenchmarkdb_w";
        
        try {
            DataSource ds = test.getDataSource(titanSvcUrl, name);
            testDataSource(ds, "select database() as id");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
    @Test
    public void testGetFromTitanServiceSuccessX() {
//        "titanDataSource"
        ApplicationContext ctx;
        try {
            ctx = new ClassPathXmlApplicationContext("spring.xml");
            DataSource ds = ctx.getBean("titanDataSource", DataSource.class);
            testDataSource(ds, "select db_name() as id");
        } catch (BeansException e) {
            Assert.fail();
        }
    }
    
    private void testDataSource(DataSource ds, String sql) {
        Connection conn;
        try {
            conn = ds.getConnection();
            Statement stat = conn.createStatement();
            stat.execute(sql);
            ResultSet rs = stat.getResultSet();
            rs.next();
            String value = rs.getString(1);
            System.out.print(value);
        } catch (SQLException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }   
}
