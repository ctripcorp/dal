package com.ctrip.framework.dal.mysql.test.cases.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ctrip.framework.dal.mysql.test.cases.AbstractTestCase;
import com.ctrip.framework.dal.mysql.test.entity.IntegrationUser;
import com.ctrip.framework.dal.mysql.test.service.UniqueKeyGenerator;
import com.ctrip.framework.dal.mysql.test.service.UserService;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Component
public class SingleUserRegisterTestCase extends AbstractTestCase {
  private static final String EMOJI_ROCKET = "\uD83D\uDE80";
  private String userName;

  @Autowired
  private UserService userService;

  @Override
  public void setUp() {
    userName = UniqueKeyGenerator.generate() + EMOJI_ROCKET;
  }

  @Override
  public void tearDown() {

  }

  @Override
  public void execute() {
    IntegrationUser user = userService.register(userName);

    check(user.getId().longValue() > 0, String.format("Invalid generated id: %d", user.getId()));
    check(user.getUserId() > 0, String.format("Invalid user id: %d", user.getUserId()));
    check(user.getUserName().equals(userName),
        String.format("Invalid user name, expected: %s, actual: %s", userName, user.getUserName()));

    int deleted = userService.deleteByUserId(user.getUserId());

    check(deleted == 1, String.format("Delete user by userId: %d failed.", user.getUserId()));
  }

  @Override
  public String description() {
    return "Single user registration";
  }
}
