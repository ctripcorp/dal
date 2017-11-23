package com.ctrip.framework.dal.demo;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import com.ctrip.framework.dal.demo.entity.App;
import com.ctrip.framework.dal.demo.mapper.AppMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

@Service
public class AppService {

  @Autowired
  private AppMapper appMapper;

  @Autowired
  @Qualifier("mysqlTransactionManager")
  private PlatformTransactionManager transactionManager;

  private Random random = new Random();


  public List<App> queryAll() {
    return Lists.newArrayList(appMapper.queryAll(0, 10));
  }

  public App queryById(long id) {
    return appMapper.queryById(id);
  }

  public List<App> queryByIdList(List<Long> idList) {
    return appMapper.queryByIdList(idList);
  }

  public App createApp(App app) {
    appMapper.insertApp(app);

    return queryById(app.getId());
  }

  public List<App> createAppList(List<App> appList) {
    appMapper.insertAppList(appList);

    List<Long> idList = FluentIterable.from(appList).transform(new Function<App, Long>() {
      @Override
      public Long apply(App input) {
        return input.getId();
      }
    }).toList();

    return queryByIdList(idList);
  }

  public App deleteAppById(long id) {
    App deleted = appMapper.queryById(id);

    if (deleted != null) {
      appMapper.deleteAppById(id);
    }

    return deleted;
  }

  public int updateAppNameUsingTransactionManager(long id, String appName) {
    DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
    definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

    TransactionStatus status = transactionManager.getTransaction(definition);
    try {
      App target = queryById(id);
      Preconditions.checkArgument(target != null, "Invalid id %d", id);

      int rowsAffected = appMapper.updateAppName(id, appName, new Timestamp(System.currentTimeMillis()));

      if (shouldRollback()) {
        System.out.println("Manually rolling back changes!!!");
        transactionManager.rollback(status);
        return 0;
      }

      System.out.println("Committing changes...");
      transactionManager.commit(status);
      return rowsAffected;
    } catch (Throwable ex) {
      transactionManager.rollback(status);
      throw ex;
    }
  }

  private boolean shouldRollback() {
    return random.nextBoolean();
  }
}
