package com.ctrip.framework.dal.demo;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ctrip.framework.dal.demo.entity.App;
import com.google.common.collect.Lists;

@RestController
@RequestMapping("/apps")
public class AppController {

  @Autowired
  private AppService appService;

  @RequestMapping(method = RequestMethod.GET)
  public List<App> getApp() {
    return appService.queryAll();
  }

  @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
  public App updateAppName(@PathVariable long id, @RequestParam("name") String appName) {
    appService.updateAppNameUsingTransactionManager(id, appName);

    return appService.queryById(id);
  }

  @RequestMapping(method = RequestMethod.POST)
  public App createApp(@RequestParam("appId") String appId, @RequestParam("name") String name,
      @RequestParam(value = "createdBy", required = false) String createdBy) {
    App app = new App();
    app.setAppId(appId);
    app.setName(name);
    app.setDatachangeCreatedby(createdBy);
    app.setDatachangeCreatedtime(new Timestamp(System.currentTimeMillis()));
    app.setDatachangeLastmodifiedby(createdBy);

    return appService.createApp(app);
  }

  @RequestMapping(value = "/batch", method = RequestMethod.POST)
  public List<App> createApps(@RequestParam("appId") String appId, @RequestParam("name") String name) {
    App app = new App();
    app.setAppId(appId);
    app.setName(name);

    App anotherApp = new App();
    anotherApp.setAppId(appId + "-shadow");
    anotherApp.setName(name);

    return appService.createAppList(Lists.newArrayList(app, anotherApp));
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public App deleteApp(@PathVariable("id") long id) {
    return appService.deleteAppById(id);
  }
}
