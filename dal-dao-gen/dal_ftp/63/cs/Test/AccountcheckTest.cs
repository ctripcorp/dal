using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.dal.test.test4.Entity.DataModel;
using com.ctrip.dal.test.test4.Interface.IDao;
using com.ctrip.dal.test.test4.Dao;

namespace com.ctrip.dal.test.test4.Test
{
    public class AccountcheckTest
    {
        public static void Test()
        {
            //以下方法的主要目的是教会您如何使用DAL
            //在实际使用的时候，您需要根据不同的情形
            //反注释相应的代码，并传入合法的参数
            //-------其他可用的方法，VS的intellisense会告诉您的---------
            IAccountcheckDao accountcheckDao = DALFactory.AccountcheckDao;

            int insertResult = accountcheckDao.InsertAccountcheck(new Accountcheck());

        /*由于没有PK，不能生成Update和Delete方法
            int updateResult = accountcheckDao.UpdateAccountcheck(new Accountcheck());

            int deleteResult = accountcheckDao.DeleteAccountcheck(new Accountcheck());

            int deleteByFieldResult = accountcheckDao.DeleteAccountcheck();

        */

            var entities = accountcheckDao.GetAll();

            long count = accountcheckDao.Count();

            var listByPage = accountcheckDao.GetListByPage(null, 0, 0);

        }
    }
}
