package com.ctrip.framework.dal.mysql.test.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ctrip.framework.dal.mysql.test.cases.TestCase;
import com.ctrip.framework.dal.mysql.test.controller.TestCaseController;
import com.ctrip.framework.dal.mysql.test.dao.IntegrationTestCaseResultDao;
import com.ctrip.framework.dal.mysql.test.entity.IntegrationTestCaseResult;
import com.ctrip.framework.dal.mysql.test.exception.TestCaseFailedException;
import com.ctrip.framework.dal.mysql.test.model.TestCaseResult;
import com.ctrip.framework.dal.mysql.test.model.TestCaseResults;
import com.ctrip.platform.dal.dao.DalHints;
import com.dianping.cat.Cat;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Service
public class TestCaseService {
  private static final Logger logger = LoggerFactory.getLogger(TestCaseController.class);
  private static final Predicate<TestCase> allTestCasePredicate = Predicates.alwaysTrue();

  private Map<String, String> testCaseDescriptions;

  @Autowired
  private List<TestCase> testCaseList;

  @Autowired
  private IntegrationTestCaseResultDao testCaseResultDao;

  @Autowired
  private Gson gson;

  private ScheduledExecutorService executorService;

  private DalHints dalHints;

  public TestCaseService() {
    dalHints = new DalHints().inShard(0);
    executorService = Executors.newSingleThreadScheduledExecutor();
  }

  @PostConstruct
  void init() {
    testCaseDescriptions = Maps.newHashMapWithExpectedSize(testCaseList.size());
    for (TestCase testCase : testCaseList) {
      testCaseDescriptions.put(testCase.name(), testCase.description());
    }

    executorService.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        runAllTests();
      }
    }, 0, 1, TimeUnit.HOURS);
  }

  public Map<String, String> listTests() {
    return testCaseDescriptions;
  }

  public TestCaseResults runAllTests() {
    return doRunTests(allTestCasePredicate);
  }

  @RequestMapping(value = "/{testNames:.+}", method = RequestMethod.POST)
  public TestCaseResults runSpecifiedTests(final List<String> testNames) {
    return doRunTests(new Predicate<TestCase>() {
      @Override
      public boolean apply(TestCase input) {
        return testNames.contains(input.name());
      }
    });
  }

  /**
   * Run the tests specified, note that this is marked as synchronized to ensure thread safety
   * 
   * @param predicate the test case filter
   * @return the test case results
   */
  private synchronized TestCaseResults doRunTests(Predicate<TestCase> predicate) {
    TestCaseResults testCaseResults = new TestCaseResults();
    for (TestCase testCase : testCaseList) {
      if (!predicate.apply(testCase)) {
        continue;
      }
      TestCaseResult result = testCaseResults.create(testCase);
      try {
        testCase.setUp();

        testCase.execute();
      } catch (Throwable ex) {
        recordError(result, String.format("Executing %s failed!", testCase.name()), ex);
      } finally {
        try {
          testCase.tearDown();
        } catch (Throwable ex) {
          recordError(result, String.format("Tearing down %s failed!", testCase.name()), ex);
        }
        result.complete();
      }
    }

    recordTestCaseResults(testCaseResults);
    return testCaseResults;
  }

  private void recordError(TestCaseResult result, String message, Throwable ex) {
    result.error(message, ex);
    logger.error(message, ex);
    if (ex instanceof TestCaseFailedException) {
      Cat.logError(ex);
    } else {
      Cat.logError(new TestCaseFailedException(message, ex));
    }
  }

  private void recordTestCaseResults(TestCaseResults testCaseResults) {
    try {
      IntegrationTestCaseResult result = new IntegrationTestCaseResult();
      result.setStatus(testCaseResults.isSuccessful() ? 1 : 0);
      result.setResult(gson.toJson(testCaseResults));

      testCaseResultDao.insert(dalHints, result);
    } catch (Throwable ex) {
      throw new TestCaseFailedException("Failed to insert test case result!", ex);
    }
  }
}
