using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.platform.uat.Entity.DataModel;
using com.ctrip.platform.uat.Interface.IDao;
using com.ctrip.platform.uat.Dao;

namespace com.ctrip.platform.uat
{
    class Program
    {
        static void Main(string[] args)
        {
        //以下方法的主要目的是教会您如何使用DAL
        //在实际使用的时候，您需要根据不同的情形
        //反注释相应的代码，并传入合法的参数
        //-------其他可用的方法，VS的intellisense会告诉您的---------
            IMoneyTest_genDao moneyTest_genDao = DALFactory.MoneyTest_genDao;

            //int result = moneyTest_genDao.InsertMoneyTest_gen(new MoneyTest_gen());
            //int result = moneyTest_genDao.UpdateMoneyTest_gen(new MoneyTest_gen());
            //int result = moneyTest_genDao.DeleteMoneyTest_gen(new MoneyTest_gen());
            //MoneyTest_gen entity = moneyTest_genDao.FindByPk(id);
            //IList<MoneyTest_gen> entities = moneyTest_genDao.GetAll();
            //long count = moneyTest_genDao.Count();
            //IList<MoneyTest_gen> listByPage = moneyTest_genDao.GetListByPage(obj, pagesize, pageno);
        }
    }
}
