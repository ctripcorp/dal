package com.ctrip.framework.dal.mysql.test.model;

import java.util.List;

import com.ctrip.framework.dal.mysql.test.cases.TestCase;
import com.google.common.collect.Lists;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class TestCaseResults {
  private List<TestCaseResult> results = Lists.newArrayList();

  public TestCaseResult create(TestCase testCase) {
    TestCaseResult result = new TestCaseResult(testCase);
    results.add(result);

    return result;
  }

  public boolean isSuccessful() {
    for (TestCaseResult result : results) {
      if (!result.isSuccessful()) {
        return false;
      }
    }

    return true;
  }
}
