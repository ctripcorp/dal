using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.platform.tools.Entity.DataModel;
using com.ctrip.platform.tools.Interface.IDao;
using com.ctrip.platform.tools.Dao;

namespace com.ctrip.platform.tools
{
    class Program
    {
        static void Main(string[] args)
        {
        //以下方法的主要目的是教会您如何使用DAL
        //在实际使用的时候，您需要根据不同的情形
        //反注释相应的代码，并传入合法的参数
        //-------其他可用的方法，VS的intellisense会告诉您的---------
            IPerson_genDao person_genDao = DALFactory.Person_genDao;

            //int result = person_genDao.InsertPerson_gen(new Person_gen());
            //int result = person_genDao.UpdatePerson_gen(new Person_gen());
            //int result = person_genDao.DeletePerson_gen(new Person_gen());
            //Person_gen entity = person_genDao.FindByPk(id);
            //IList<Person_gen> entities = person_genDao.GetAll();
            //long count = person_genDao.Count();
            //IList<Person_gen> listByPage = person_genDao.GetListByPage(obj, pagesize, pageno);
            Isysdiagrams_genDao sysdiagrams_genDao = DALFactory.sysdiagrams_genDao;

            //int result = sysdiagrams_genDao.Insertsysdiagrams_gen(new sysdiagrams_gen());
            //int result = sysdiagrams_genDao.Updatesysdiagrams_gen(new sysdiagrams_gen());
            //int result = sysdiagrams_genDao.Deletesysdiagrams_gen(new sysdiagrams_gen());
            //sysdiagrams_gen entity = sysdiagrams_genDao.FindByPk(id);
            //IList<sysdiagrams_gen> entities = sysdiagrams_genDao.GetAll();
            //long count = sysdiagrams_genDao.Count();
            //IList<sysdiagrams_gen> listByPage = sysdiagrams_genDao.GetListByPage(obj, pagesize, pageno);
        }
    }
}
