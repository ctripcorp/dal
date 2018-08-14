package com.ctrip.platform.dal.application.web;

import com.ctrip.platform.dal.application.entity.DALServiceTable;
import com.ctrip.platform.dal.application.service.DALService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/")
public class DALServiceController {

  @Autowired
  private DALService dalService;


  @RequestMapping("/queryMySql")
  public List<DALServiceTable> getMySql() throws Exception{
    return dalService.queryAllMySql();
  }

  @RequestMapping("/querySqlServer")
  public List<DALServiceTable> getSqlServer() throws Exception{
    return dalService.queryAllSqlServer();
  }

  @RequestMapping("/updateMySql")
  public DALServiceTable updateMysql() throws Exception{
    DALServiceTable pojo=new DALServiceTable();
    pojo.setID(1);
    pojo.setName("updateMySqlTest");
    return dalService.updateMySql(pojo);
  }

  @RequestMapping("/updateSqlServer")
  public DALServiceTable updateSqlServer() throws Exception{
    DALServiceTable pojo=new DALServiceTable();
    pojo.setID(1);
    pojo.setName("updateSqlServer");
    return dalService.updateSqlServer(pojo);
  }

  @RequestMapping("/insertMySql")
  public DALServiceTable insertMySql() throws Exception{
    DALServiceTable testPojo=new DALServiceTable();
    testPojo.setName("insertTestPojo");
    return dalService.insertMySql(testPojo);
  }

  @RequestMapping("/insertSqlServer")
  public DALServiceTable insertSqlServer() throws Exception{
    DALServiceTable testPojo=new DALServiceTable();
    testPojo.setName("insertTestPojo");
    return dalService.insertSqlServer(testPojo);
  }

  @RequestMapping("/welcome")
  public ModelAndView welcomePage(){
    return new ModelAndView("welcome");
  }

  @RequestMapping("/mysql")
  public void testMysql() throws Exception{
    DALServiceTable testPojo=new DALServiceTable();
    testPojo.setName("testInsert");
    dalService.insertMySql(testPojo);
    testPojo.setName("testUpdate");
    dalService.updateMySql(testPojo);
    dalService.deleteMySql(testPojo);
  }

  @RequestMapping("/sqlserver")
  public void testSqlServer() throws Exception{
    DALServiceTable testPojo=new DALServiceTable();
    testPojo.setName("testInsert");
    dalService.insertSqlServer(testPojo);
    testPojo.setName("testUpdate");
    dalService.updateSqlServer(testPojo);
    dalService.deleteSqlServer(testPojo);
  }
}
