using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using hjhTest.Entity.DataModel;
using hjhTest.Interface.IDao;
using hjhTest.Dao;

namespace hjhTest
{
    class Program
    {
        static void Main(string[] args)
        {
        //以下方法的主要目的是教会您如何使用DAL
        //在实际使用的时候，您需要根据不同的情形
        //反注释相应的代码，并传入合法的参数
        //-------其他可用的方法，VS的intellisense会告诉您的---------
            IManyTypes_genDao manyTypes_genDao = DALFactory.ManyTypes_genDao;

            //int result = manyTypes_genDao.InsertManyTypes_gen(new ManyTypes_gen());
            //int result = manyTypes_genDao.UpdateManyTypes_gen(new ManyTypes_gen());
            //int result = manyTypes_genDao.DeleteManyTypes_gen(new ManyTypes_gen());
            //ManyTypes_gen entity = manyTypes_genDao.FindByPk(id);
            //IList<ManyTypes_gen> entities = manyTypes_genDao.GetAll();
            //long count = manyTypes_genDao.Count();
            //IList<ManyTypes_gen> listByPage = manyTypes_genDao.GetListByPage(obj, pagesize, pageno);
            IMoneyTest_genDao moneyTest_genDao = DALFactory.MoneyTest_genDao;

            //int result = moneyTest_genDao.InsertMoneyTest_gen(new MoneyTest_gen());
            //int result = moneyTest_genDao.UpdateMoneyTest_gen(new MoneyTest_gen());
            //int result = moneyTest_genDao.DeleteMoneyTest_gen(new MoneyTest_gen());
            //MoneyTest_gen entity = moneyTest_genDao.FindByPk(id);
            //IList<MoneyTest_gen> entities = moneyTest_genDao.GetAll();
            //long count = moneyTest_genDao.Count();
            //IList<MoneyTest_gen> listByPage = moneyTest_genDao.GetListByPage(obj, pagesize, pageno);
            IPerson_genDao person_genDao = DALFactory.Person_genDao;

            //int result = person_genDao.InsertPerson_gen(new Person_gen());
            //int result = person_genDao.UpdatePerson_gen(new Person_gen());
            //int result = person_genDao.DeletePerson_gen(new Person_gen());
            //Person_gen entity = person_genDao.FindByPk(id);
            //IList<Person_gen> entities = person_genDao.GetAll();
            //long count = person_genDao.Count();
            //IList<Person_gen> listByPage = person_genDao.GetListByPage(obj, pagesize, pageno);
        }
    }
}
