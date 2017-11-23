package com.ctrip.framework.dal.mysql.test.exception;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class TestCaseFailedException extends RuntimeException {

  public TestCaseFailedException(String message) {
    super(message);
  }

  public TestCaseFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
