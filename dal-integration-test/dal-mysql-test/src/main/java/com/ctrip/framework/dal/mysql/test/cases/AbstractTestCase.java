package com.ctrip.framework.dal.mysql.test.cases;

import com.ctrip.framework.dal.mysql.test.exception.TestCaseFailedException;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public abstract class AbstractTestCase implements TestCase {

  protected void check(boolean expression, String message) {
    if (!expression) {
      reportError(message);
    }
  }

  protected void reportError(String message) {
    throw new TestCaseFailedException(message);
  }

  protected void reportError(String message, Throwable ex) {
    throw new TestCaseFailedException(message, ex);
  }

  @Override
  public String name() {
    return this.getClass().getSimpleName();
  }
}
