package com.ctrip.platform.dal.application.service;

import com.ctrip.platform.dal.application.dao.DALServiceDao;
import com.ctrip.platform.dal.application.entity.DALServiceTable;

import com.ctrip.platform.dal.dao.DalHints;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DALService {

//  @Autowired
  private DALServiceDao mySqlDao = null;
  @Autowired
  private DALServiceDao clusterDao;
  @Autowired
  private DALServiceDao sqlServerDao;

  private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  public List<DALServiceTable> queryAllMySql() throws Exception {
      return clusterDao.queryAll(null);
  }

  public List<DALServiceTable> queryAllSqlServer() throws Exception {
    return sqlServerDao.queryAll(null);
  }

  public DALServiceTable queryMySql(DALServiceTable pojo) throws Exception {
      return clusterDao.queryByPk(pojo,null);
  }

  public DALServiceTable querySqlServer(DALServiceTable pojo) throws Exception {
    return sqlServerDao.queryByPk(pojo,null);
  }

  public void deleteMySql(DALServiceTable pojo) throws Exception {
    clusterDao.delete(null,pojo);
  }

  public void deleteSqlServer(DALServiceTable pojo) throws Exception {
    sqlServerDao.delete(null,pojo);
  }

  public DALServiceTable updateMySql(DALServiceTable pojo) throws Exception {
    clusterDao.update(null,pojo);
    return queryMySql(pojo);
  }

  public DALServiceTable updateSqlServer(DALServiceTable pojo) throws Exception {
    sqlServerDao.update(null,pojo);
    return querySqlServer(pojo);
  }

  public DALServiceTable insertMySql(DALServiceTable pojo) throws Exception {
    mySqlDao.insert(new DalHints().setIdentityBack(),pojo);
    return mySqlDao.queryByPk(pojo,null);
  }

  public DALServiceTable insertCluster(DALServiceTable pojo) throws Exception {
    clusterDao.insert(new DalHints().setIdentityBack(),pojo);
    return clusterDao.queryByPk(pojo,null);
  }

  public DALServiceTable insertSqlServer(DALServiceTable pojo) throws Exception {
    sqlServerDao.insert(new DalHints().setIdentityBack(),pojo);
    return sqlServerDao.queryByPk(pojo,null);
  }

  public int deleteMySqlAll() throws Exception{
    return clusterDao.deleteAll();
  }

  public int deleteSqlServerAll() throws Exception{
    return sqlServerDao.deleteAll();
  }

  public List<DALServiceTable> queryAtPageWithoutOrderBy() throws Exception{
    List<Integer> age=new ArrayList<>();
    age.add(20);
    age.add(20);
    return sqlServerDao.queryAtPageWithoutOrderBy(age,new DalHints(),10);
  }

  public List<DALServiceTable> queryAtPageWithOrderBy() throws Exception{
    List<Integer> age=new ArrayList<>();
    age.add(20);
    age.add(20);
    return sqlServerDao.queryAtPageWithOrderby(age,new DalHints(),10);
  }

  public List<String> queryTopWithOrderby() throws Exception{
    List<Integer> age=new ArrayList<>();
    age.add(20);
    age.add(20);
    return sqlServerDao.queryTopWithOrderby(age,new DalHints(),10);
  }

  public List<String> queryTopWithNoOrderby() throws Exception{
    List<Integer> age=new ArrayList<>();
    age.add(20);
    age.add(20);
    return sqlServerDao.queryTopWithNoOrderby(age,new DalHints(),10);
  }

  public void mockCatTransaction(String type, String name, long millis) {
    executor.submit(() -> {
      Transaction t = Cat.newTransaction(type, name);
      try {
        Thread.sleep(millis);
        t.setStatus(Transaction.SUCCESS);
      } catch (Exception e) {
        Cat.logError(e);
        t.setStatus(e);
      } finally {
        t.complete();
      }
    });
  }

}
