package com.ctrip.platform.dal.application.service;

import com.ctrip.platform.dal.application.dao.DALServiceDao;
import com.ctrip.platform.dal.application.entity.DALServiceTable;

import com.ctrip.platform.dal.dao.DalHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class DALService {

  @Autowired
  private DALServiceDao mySqlDao;

  @Autowired
  private DALServiceDao sqlServerDao;

  public List<DALServiceTable> queryAllMySql() throws Exception {
      return mySqlDao.queryAll(null);
  }

  public List<DALServiceTable> queryAllSqlServer() throws Exception {
    return sqlServerDao.queryAll(null);
  }

  public DALServiceTable queryMySql(DALServiceTable pojo) throws Exception {
      return mySqlDao.queryByPk(pojo,null);
  }

  public DALServiceTable querySqlServer(DALServiceTable pojo) throws Exception {
    return sqlServerDao.queryByPk(pojo,null);
  }

  public void deleteMySql(DALServiceTable pojo) throws Exception {
    mySqlDao.delete(null,pojo);
  }

  public void deleteSqlServer(DALServiceTable pojo) throws Exception {
    sqlServerDao.delete(null,pojo);
  }

  public DALServiceTable updateMySql(DALServiceTable pojo) throws Exception {
    mySqlDao.update(null,pojo);
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

  public DALServiceTable insertSqlServer(DALServiceTable pojo) throws Exception {
    sqlServerDao.insert(new DalHints().setIdentityBack(),pojo);
    return sqlServerDao.queryByPk(pojo,null);
  }

  public int deleteMySqlAll() throws Exception{
    return mySqlDao.deleteAll();
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
}
