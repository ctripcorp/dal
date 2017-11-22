package com.ctrip.framework.dal.mysql.test.cases;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface TestCase {

  /**
   * do some set up
   */
  void setUp();

  /**
   * clean up
   */
  void tearDown();

  /**
   * execute the test
   */
  void execute();

  /**
   * @return the test description
   */
  String description();

  /**
   * @return the test name
   */
  String name();
}
