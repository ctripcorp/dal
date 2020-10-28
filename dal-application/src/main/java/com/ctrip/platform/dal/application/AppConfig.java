package com.ctrip.platform.dal.application;


import com.ctrip.platform.dal.application.dao.DALServiceDao;
import com.ctrip.platform.dal.application.service.DALService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;


@Configuration
@ComponentScan(basePackages = "com.ctrip.platform.dal.application")
public class AppConfig {

//    @Bean
//    public DALServiceDao dao() throws SQLException {
//        return new DALServiceDao();
//    }

//    @Bean
//    public DALServiceDao mySqlDao() throws SQLException {
//        return new DALServiceDao("DalMySqlTest");
//    }

    @Bean
    public DALServiceDao clusterDao() throws SQLException {
        return new DALServiceDao("dbadalclustertest01db");
    }

    @Bean
    public DALServiceDao sqlServerDao() throws SQLException {
        return new DALServiceDao("DalSQLServerTest");
    }

    @Bean
    public DALService dalService() throws SQLException {
        return new DALService();
    }

//    @Bean
//    public DALRequestTask dalRequestTask() {
//        return new DALRequestTask();
//    }
//
//    @Bean
//    public DalApplicationConfig dalApplicationConfig() {
//        return new DalApplicationConfig();
//    }


//  @Bean
//  public DalClient dalClient() {
//    return DalClientFactory.getClient("DalService2DB_W");
//  }

//  @Bean
//  public ServletListenerRegistrationBean dalListener() {
//    return new ServletListenerRegistrationBean<>(new DalClientFactoryListener());
//  }
}
