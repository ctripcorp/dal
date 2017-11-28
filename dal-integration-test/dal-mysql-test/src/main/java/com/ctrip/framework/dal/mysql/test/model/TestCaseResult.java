package com.ctrip.framework.dal.mysql.test.model;

import com.ctrip.framework.dal.mysql.test.cases.TestCase;
import com.ctrip.framework.dal.mysql.test.exception.ExceptionUtil;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class TestCaseResult {

  private final String name;
  private final String description;
  private transient long startInMills;
  private long elapsedInMills;
  private boolean successful;
  private String message;

  public TestCaseResult(TestCase testCase) {
    name = testCase.name();
    description = testCase.description();
    successful = true;
    startInMills = System.currentTimeMillis();
  }

  public void complete() {
    elapsedInMills = System.currentTimeMillis() - startInMills;
  }

  public void error(String message, Throwable ex) {
    this.successful = false;
    this.message = String.format("%s Exceptions: %s", message, ExceptionUtil.getDetailMessage(ex));
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public long getElapsedInMills() {
    return elapsedInMills;
  }

  public boolean isSuccessful() {
    return successful;
  }

  public String getMessage() {
    return message;
  }
}
