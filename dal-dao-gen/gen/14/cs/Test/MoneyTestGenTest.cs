using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using DAL.Entity.DataModel;
using DAL.Interface.IDao;
using DAL.Dao;

namespace DAL.Test
{
    public class JustQueryTest
    {
        public static void Test()
        {
            //以下方法的主要目的是教会您如何使用DAL
            //在实际使用的时候，您需要根据不同的情形
            //反注释相应的代码，并传入合法的参数
            //-------其他可用的方法，VS的intellisense会告诉您的---------
            IJustQueryDao justQueryDao = DALFactory.JustQueryDao;

            int insertResult = justQueryDao.InsertJustQuery(new JustQuery());

            int updateResult = justQueryDao.UpdateJustQuery(new JustQuery());

            int deleteResult = justQueryDao.DeleteJustQuery(new JustQuery());


            var entities = justQueryDao.GetAll();

            long count = justQueryDao.Count();

        }
    }
}
