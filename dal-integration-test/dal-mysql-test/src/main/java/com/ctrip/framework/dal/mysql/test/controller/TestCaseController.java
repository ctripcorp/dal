package com.ctrip.framework.dal.mysql.test.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ctrip.framework.dal.mysql.test.model.TestCaseResults;
import com.ctrip.framework.dal.mysql.test.service.TestCaseService;
import com.google.common.base.Splitter;

@RestController
@RequestMapping("/tests")
public class TestCaseController {
  private static final Splitter commaSplitter = Splitter.on(",").omitEmptyStrings().trimResults();

  @Autowired
  private TestCaseService testCaseService;

  @RequestMapping(method = RequestMethod.GET)
  public Map<String, String> listTests() {
    return testCaseService.listTests();
  }

  @RequestMapping(method = RequestMethod.POST)
  public TestCaseResults runAllTests() {
    return testCaseService.runAllTests();
  }

  @RequestMapping(value = "/{testNames:.+}", method = RequestMethod.POST)
  public TestCaseResults runSpecifiedTest(@PathVariable final String testNames) {
    final List<String> testNameList = commaSplitter.splitToList(testNames);
    return testCaseService.runSpecifiedTests(testNameList);
  }
}
