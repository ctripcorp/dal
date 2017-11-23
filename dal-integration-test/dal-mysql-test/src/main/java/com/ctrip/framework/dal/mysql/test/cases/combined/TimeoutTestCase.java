package com.ctrip.framework.dal.mysql.test.cases.combined;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ctrip.framework.dal.mysql.test.cases.AbstractTestCase;
import com.ctrip.framework.dal.mysql.test.dao.IntegrationTestCaseResultDao;
import com.ctrip.platform.dal.dao.DalHints;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Component
public class TimeoutTestCase extends AbstractTestCase {

  @Autowired
  private IntegrationTestCaseResultDao testCaseResultDao;
  private DalHints dalHints = new DalHints().inShard(0).timeout(1);

  @Override
  public void setUp() {

  }

  @Override
  public void tearDown() {

  }

  @Override
  public void execute() {
    try {
      testCaseResultDao.queryWithTimeout(2, dalHints);

      check(false, "Query with timeout should failed!");
    } catch (Throwable ex) {
      check(ex.getCause() instanceof MySQLTimeoutException, String.format("Invalid cause: %s", ex.getCause()));
    }
  }

  @Override
  public String description() {
    return "Timeout query case";
  }
}
