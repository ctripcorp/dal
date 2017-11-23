package com.ctrip.framework.dal.mysql.test.cases.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ctrip.framework.dal.mysql.test.cases.AbstractTestCase;
import com.ctrip.framework.dal.mysql.test.service.UniqueKeyGenerator;
import com.ctrip.framework.dal.mysql.test.service.UserService;
import com.google.common.collect.Lists;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Component
public class BatchUserRegisterFailedTestCase extends AbstractTestCase {
  @Autowired
  private UserService userService;

  private List<String> userNames;

  @Override
  public void setUp() {
    String userName = UniqueKeyGenerator.generate();

    userNames = Lists.newArrayList();
    // duplicate user names
    userNames.add(userName);
    userNames.add(userName);
    userNames.add(userName);
    userNames.add(userName);
  }

  @Override
  public void tearDown() {

  }

  @Override
  public void execute() {
    try {
      userService.register(userNames);

      check(1 == 0, "Creating users with duplicated names should not succeed!");
    } catch (Throwable ex) {
      // ignore
    }

    check(userService.queryByUserName(userNames.get(0)) == null,
        "Creating users with duplicated names should not succeed!");
  }

  @Override
  public String description() {
    return "Batch register users with duplicated names";
  }
}
