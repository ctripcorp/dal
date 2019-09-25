package com.ctrip.platform.dal.application.web;

import com.ctrip.platform.dal.application.entity.DALServiceTable;
import com.ctrip.platform.dal.application.service.DALService;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RestController
@RequestMapping("/")
public class DALServiceController {

    @Autowired
    private DALService dalService;


    @RequestMapping("/queryMySql")
    public List<DALServiceTable> getMySql() throws Exception {
        return dalService.queryAllMySql();
    }

    @RequestMapping("/querySqlServer")
    public List<DALServiceTable> getSqlServer() throws Exception {
        return dalService.queryAllSqlServer();
    }

    @RequestMapping("/updateMySql")
    public DALServiceTable updateMysql() throws Exception {
        DALServiceTable pojo = new DALServiceTable();
        pojo.setID(1);
        pojo.setName("updateMySqlTest");
        return dalService.updateMySql(pojo);
    }

    @RequestMapping("/updateSqlServer")
    public DALServiceTable updateSqlServer() throws Exception {
        DALServiceTable pojo = new DALServiceTable();
        pojo.setID(1);
        pojo.setName("updateSqlServer");
        return dalService.updateSqlServer(pojo);
    }

    @RequestMapping("/insertMySql")
    public DALServiceTable insertMySql() throws Exception {
        DALServiceTable testPojo = new DALServiceTable();
        testPojo.setName("insertTestPojo");
        return dalService.insertMySql(testPojo);
    }

    @RequestMapping("/insertSqlServer")
    public DALServiceTable insertSqlServer() throws Exception {
        DALServiceTable testPojo = new DALServiceTable();
        testPojo.setName("insertTestPojo");
        return dalService.insertSqlServer(testPojo);
    }

    @RequestMapping("/welcome")
    public ModelAndView welcomePage() {
        return new ModelAndView("welcome");
    }

    @RequestMapping("/deleteMySqlAll")
    public int deleteMySqlAll() throws Exception {
        return dalService.deleteMySqlAll();
    }

    @RequestMapping("/deleteSqlServerAll")
    public int deleteSqlServerAll() throws Exception {
        return dalService.deleteSqlServerAll();
    }

    @RequestMapping("/queryAtPageWithoutOrderBy")
    public List<DALServiceTable> queryAtPageWithoutOrderBy() throws Exception {
        return dalService.queryAtPageWithoutOrderBy();
    }

    @RequestMapping("/queryAtPageWithOrderBy")
    public List<DALServiceTable> queryAtPageWithOrderBy() throws Exception {
        return dalService.queryAtPageWithOrderBy();
    }

    @RequestMapping("/queryTopWithOrderby")
    public List<String> queryTopWithOrderby() throws Exception {
        return dalService.queryTopWithOrderby();
    }

    @RequestMapping("/queryTopWithNoOrderby")
    public List<String> queryTopWithNoOrderby() throws Exception {
        return dalService.queryTopWithNoOrderby();
    }

    @RequestMapping("/mockPoolWaitAlert")
    public String mockPoolWaitAlert(@RequestParam(name = "millis", required = false) Long millis) {
        String type = "DAL.alert.poolWait";
        String name = "Connection::waitConnection:mock";
        return mockCatTransaction(type, name, millis);
    }

    @RequestMapping("/mockCatTransaction")
    public String mockCatTransaction(@RequestParam(name = "type", required = false) String type,
                                     @RequestParam(name = "name", required = false) String name,
                                     @RequestParam(name = "millis", required = false) Long millis) {
        millis = millis == null ? 100 : millis;
        dalService.mockCatTransaction(type, name, millis);
        return "ok";
    }

    @RequestMapping(value = "/unhealthy")
    @ResponseBody
    public String healthCheckException() throws Exception {
        throw new Exception("test unhealthy");
    }

    @RequestMapping(value = "/healthy")
    @ResponseBody
    public String healthCheck() throws Exception {
        return "ok";
    }

    @RequestMapping(value = "/healthyBlank")
    @ResponseBody
    public void healthCheckBlank() throws Exception {
        int i=80,j=80;
    }
}
