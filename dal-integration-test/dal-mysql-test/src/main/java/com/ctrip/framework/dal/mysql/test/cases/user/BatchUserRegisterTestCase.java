package com.ctrip.framework.dal.mysql.test.cases.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ctrip.framework.dal.mysql.test.cases.AbstractTestCase;
import com.ctrip.framework.dal.mysql.test.entity.IntegrationUser;
import com.ctrip.framework.dal.mysql.test.service.UniqueKeyGenerator;
import com.ctrip.framework.dal.mysql.test.service.UserService;
import com.google.common.collect.Lists;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Component
public class BatchUserRegisterTestCase extends AbstractTestCase {
  @Autowired
  private UserService userService;

  private List<String> userNames;

  @Override
  public void setUp() {
    userNames = Lists.newArrayList();
    userNames.add(UniqueKeyGenerator.generate());
    userNames.add(UniqueKeyGenerator.generate());
  }

  @Override
  public void tearDown() {

  }

  @Override
  public void execute() {
    List<IntegrationUser> users = userService.register(userNames);

    check(users.size() == 2, String.format("Invalid registered users: %d", users.size()));

    List<Long> userIdList = Lists.newArrayList();
    for (IntegrationUser user : users) {
      checkUser(user);
      userIdList.add(user.getUserId());
    }

    int[] deleted = userService.deleteByUserIdList(userIdList);

    check(deleted.length == 2, String.format("Delete user failed: %s", userIdList));

    for (int i = 0; i < deleted.length; i++) {
      check(deleted[i] == 1, String.format("Delete for index %d failed", i));
    }
  }

  private void checkUser(IntegrationUser user) {
    check(user.getId().longValue() > 0, String.format("Invalid generated id: %d", user.getId()));
    check(user.getUserId() > 0, String.format("Invalid user id: %d", user.getUserId()));
  }

  @Override
  public String description() {
    return "Batch user registrations";
  }
}
