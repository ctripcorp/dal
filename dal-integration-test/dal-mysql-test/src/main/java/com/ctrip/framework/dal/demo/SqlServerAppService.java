package com.ctrip.framework.dal.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ctrip.framework.dal.demo.entity.App;
import com.ctrip.framework.dal.demo.mapper.SqlServerAppMapper;

@Service
public class SqlServerAppService {

  @Autowired
  private SqlServerAppMapper appMapper;

  public List<App> queryAll() {
    return appMapper.queryAll(0, 10);
  }

  public App queryById(long id) {
    return appMapper.queryById(id);
  }

  public List<App> queryByIdList(List<Long> idList) {
    return appMapper.queryByIdList(idList);
  }

  public App createApp(App app) {
    appMapper.insertApp(app);

    return queryById(app.getId());
  }

  public List<App> createAppList(List<App> appList) {
    appMapper.insertAppList(appList);

    return appMapper.queryAll(0, 999);
  }

  public App updateAppName(long id, String appName) {
    appMapper.updateAppName(id, appName);

    return queryById(id);
  }

  public App deleteAppById(long id) {
    App deleted = appMapper.queryById(id);

    if (deleted != null) {
      appMapper.deleteAppById(id);
    }

    return deleted;
  }
}
