package com.ctrip.framework.dal.mysql.test.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ctrip.framework.dal.mysql.test.dao.IntegrationUserDao;
import com.ctrip.framework.dal.mysql.test.entity.IntegrationUser;
import com.ctrip.platform.dal.dao.DalHints;
import com.google.common.collect.Lists;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Service
public class UserService {

  @Autowired
  private IdGenerator idGenerator;

  @Autowired
  private IntegrationUserDao userDao;

  private DalHints inAllShardsDalHints;
  private DalHints shardByUserIdDalHints;

  public UserService() {
    inAllShardsDalHints = new DalHints();
    inAllShardsDalHints.inAllShards();

    shardByUserIdDalHints = new DalHints();
    shardByUserIdDalHints.shardBy("UserId");
  }

  public IntegrationUser register(String userName) {
    IntegrationUser user = assembleUser(userName);

    try {
      userDao.insert(user);
      return queryByUserId(user.getUserId());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public List<IntegrationUser> register(List<String> userNames) {
    List<IntegrationUser> users = Lists.newArrayList();
    List<Long> userIdList = Lists.newArrayList();

    for (String userName : userNames) {
      IntegrationUser user = assembleUser(userName);
      users.add(user);
      userIdList.add(user.getUserId());
    }

    try {
      userDao.batchInsert(users);

      return queryByUserIdList(userIdList);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public IntegrationUser queryByUserId(long userId) {
    try {
      return userDao.queryByUserId(userId);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public List<IntegrationUser> queryByUserIdList(List<Long> userIdList) {
    try {
      // TODO why is shard by user hints required?
      return userDao.queryByUserIdList(userIdList, shardByUserIdDalHints);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public IntegrationUser queryByUserName(String userName) {
    try {
      return userDao.queryByUserName(userName);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public IntegrationUser rename(long userId, String newUserName) {
    try {
      IntegrationUser user = queryByUserId(userId);
      user.setUserName(newUserName);

      userDao.update(user);

      return queryByUserName(newUserName);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public int deleteByUserId(long userId) {
    try {
      return userDao.delete(queryByUserId(userId));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public int[] deleteByUserIdList(List<Long> userIdList) {
    try {
      return userDao.delete(queryByUserIdList(userIdList));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private IntegrationUser assembleUser(String userName) {
    IntegrationUser user = new IntegrationUser();
    user.setUserId(idGenerator.nextUserId());
    user.setUserName(userName);

    return user;
  }
}
