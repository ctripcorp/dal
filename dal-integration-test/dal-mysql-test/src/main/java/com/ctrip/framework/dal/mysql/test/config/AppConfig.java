package com.ctrip.framework.dal.mysql.test.config;

import com.ctrip.framework.dal.mysql.test.service.OrderServiceWithTransactional;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import java.sql.SQLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ctrip.framework.dal.mysql.test.dao.IntegrationOrderDao;
import com.ctrip.framework.dal.mysql.test.dao.IntegrationOrderDetailDao;
import com.ctrip.framework.dal.mysql.test.dao.IntegrationOrderIdSeedDao;
import com.ctrip.framework.dal.mysql.test.dao.IntegrationTestCaseResultDao;
import com.ctrip.framework.dal.mysql.test.dao.IntegrationUserDao;
import com.ctrip.framework.dal.mysql.test.dao.IntegrationUserIdSeedDao;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;

@Configuration
public class AppConfig {
  @Bean
  public IntegrationUserDao userDao() throws SQLException {
    return new IntegrationUserDao();
  }

  @Bean
  public IntegrationUserIdSeedDao userIdSeedDao() throws SQLException {
    return new IntegrationUserIdSeedDao();
  }

  @Bean
  public IntegrationOrderIdSeedDao orderIdSeedDao() throws SQLException {
    return new IntegrationOrderIdSeedDao();
  }

  @Bean
  public IntegrationOrderDao orderDao() throws SQLException {
    return new IntegrationOrderDao();
  }

  @Bean
  public IntegrationOrderDetailDao orderDetailDao() throws SQLException {
    return new IntegrationOrderDetailDao();
  }

  @Bean
  public IntegrationTestCaseResultDao testCaseResultDao() throws SQLException {
    return new IntegrationTestCaseResultDao();
  }

  @Bean("dalServiceDBClient")
  public DalClient dalServiceDBClient() {
    return DalClientFactory.getClient("DalServiceDB");
  }

  @Bean
  public OrderServiceWithTransactional orderServiceWithTransactional()
      throws IllegalAccessException, InstantiationException {
    return DalTransactionManager.create(OrderServiceWithTransactional.class);
  }
}
