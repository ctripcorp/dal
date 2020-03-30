package com.ctrip.platform.dal.application.web;

import com.ctrip.platform.dal.application.entity.DALServiceTable;
import com.ctrip.platform.dal.application.service.DALDataSourceService;
import com.ctrip.platform.dal.application.service.DALService;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.mysql.jdbc.NonRegisteringDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RestController
@RequestMapping("/")
public class DALServiceController {

    @Autowired
    private DALService dalService;
    @Autowired
    private DALDataSourceService dalDataSourceService;

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

    @RequestMapping("/")
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

    @RequestMapping("/countWithDataSource")
    public ResponseVo countWithDataSource(@RequestParam(name = "ds") String ds) throws Exception {
        ResponseVo res = new ResponseVo();
        try {
            String result = dalDataSourceService.count(ds);
            res.setCode(0);
            res.setMessage("Success");
            res.setData(result);
        } catch (Exception e) {
            res.setCode(-1);
            res.setMessage(String.format("Fail: \n%s", e));
        }
        return res;
    }

    @RequestMapping("/insertWithDataSource")
    public ResponseVo insertWithDataSource(@RequestParam(name = "ds") String ds,
                                           @RequestParam(name = "val") String val) throws Exception {
        ResponseVo res = new ResponseVo();
        try {
            String result = dalDataSourceService.insert(ds, val);
            res.setCode(0);
            res.setMessage("Success");
            res.setData(result);
        } catch (Exception e) {
            res.setCode(-1);
            res.setMessage(String.format("Fail: \n%s", e));
        }
        return res;
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

    @RequestMapping(value = "/healthStatus")
    public void healthCheck(HttpServletRequest request, HttpServletResponse response, @RequestParam boolean needFail) throws Exception {
        response.setHeader("Content-type", "application/json");
        if(!needFail){
            response.getWriter().write("ok");
        }else {
            response.getWriter().write("fail");
            response.setStatus(555);
        }
    }

    @RequestMapping(value = "/mgr_oy")
    public void mgrConnectionTestOY(HttpServletResponse response) throws Exception {
        Properties connProps = new Properties();
        String hostName = "address=(type=master)(protocol=tcp)(host=10.25.82.86)(port=55944):3306";
        String portNumber = "3306";
        connProps.setProperty("user", "m_fxdalcluster");
        connProps.setProperty("password", "6]wWaglbqiSjlxm6qilx");
        connProps.setProperty(NonRegisteringDriver.HOST_PROPERTY_KEY, hostName);
        connProps.setProperty(NonRegisteringDriver.PORT_PROPERTY_KEY, portNumber);
        connProps.setProperty(NonRegisteringDriver.HOST_PROPERTY_KEY + ".1", hostName);
        connProps.setProperty(NonRegisteringDriver.PORT_PROPERTY_KEY + ".1", portNumber);
        connProps.setProperty(NonRegisteringDriver.NUM_HOSTS_PROPERTY_KEY, "1");
        connProps.setProperty("roundRobinLoadBalance", "false");
        String url = "jdbc:mysql://" + hostName + ":" + portNumber + "/";

        try {
            NonRegisteringDriver driver = new NonRegisteringDriver();
            Connection con = driver.connect(url, connProps);
            response.getWriter().write("success");
        } catch (Exception e) {
            throw e;
        }
    }

    @RequestMapping(value = "/mgr_rb")
    public void mgrConnectionTestRB(HttpServletResponse response) throws IOException {
        Properties connProps = new Properties();
        String hostName = "address=(type=master)(protocol=tcp)(host=10.60.46.16)(port=55944):3306";
        String portNumber = "3306";
        connProps.setProperty("user", "m_fxdalcluster");
        connProps.setProperty("password", "6]wWaglbqiSjlxm6qilx");
        connProps.setProperty(NonRegisteringDriver.HOST_PROPERTY_KEY, hostName);
        connProps.setProperty(NonRegisteringDriver.PORT_PROPERTY_KEY, portNumber);
        connProps.setProperty(NonRegisteringDriver.HOST_PROPERTY_KEY + ".1", hostName);
        connProps.setProperty(NonRegisteringDriver.PORT_PROPERTY_KEY + ".1", portNumber);
        connProps.setProperty(NonRegisteringDriver.NUM_HOSTS_PROPERTY_KEY, "1");
        connProps.setProperty("roundRobinLoadBalance", "false");
        String url = "jdbc:mysql://" + hostName + ":" + portNumber + "/";

        try {
            NonRegisteringDriver driver = new NonRegisteringDriver();
            Connection con = driver.connect(url, connProps);
            response.getWriter().write("success");
        } catch (Exception e) {
            response.getWriter().write(e.getMessage());
        }
    }
}
