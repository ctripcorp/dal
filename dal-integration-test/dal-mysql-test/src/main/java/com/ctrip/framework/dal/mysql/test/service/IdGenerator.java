package com.ctrip.framework.dal.mysql.test.service;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ctrip.framework.dal.mysql.test.dao.IntegrationOrderIdSeedDao;
import com.ctrip.framework.dal.mysql.test.dao.IntegrationUserIdSeedDao;
import com.ctrip.framework.dal.mysql.test.entity.IntegrationOrderIdSeed;
import com.ctrip.framework.dal.mysql.test.entity.IntegrationUserIdSeed;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.dianping.cat.Cat;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Service
public class IdGenerator {
  private static final Logger logger = LoggerFactory.getLogger(IdGenerator.class);

  @Autowired
  private IntegrationUserIdSeedDao userIdSeedDao;

  @Autowired
  private IntegrationOrderIdSeedDao orderIdSeedDao;

  private ScheduledExecutorService executorService;
  private DalHints dalHints;

  public IdGenerator() {
    dalHints = new DalHints();
    dalHints.inShard(0);

    executorService = Executors.newSingleThreadScheduledExecutor();
  }

  @PostConstruct
  void init() {
    executorService.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        try {
          userIdSeedDao.batchDelete(dalHints, userIdSeedDao.queryAll(dalHints));
          orderIdSeedDao.batchDelete(dalHints, orderIdSeedDao.queryAll(dalHints));
        } catch (Throwable e) {
          logger.error("Clearing generated id failed", e);
          Cat.logError(e);
        }
      }
    }, 0, 5, TimeUnit.MINUTES);
  }

  public long nextUserId() {
    KeyHolder keyHolder = new KeyHolder();
    try {
      userIdSeedDao.insert(dalHints, keyHolder, new IntegrationUserIdSeed());

      return keyHolder.getKey().longValue();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public long nextOrderId() {
    KeyHolder keyHolder = new KeyHolder();
    try {
      orderIdSeedDao.insert(dalHints, keyHolder, new IntegrationOrderIdSeed());

      return keyHolder.getKey().longValue();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
