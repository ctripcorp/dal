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
    public class AbacusAirBookLogGenTest
    {
        public static void Test()
        {
            //以下方法的主要目的是教会您如何使用DAL
            //在实际使用的时候，您需要根据不同的情形
            //反注释相应的代码，并传入合法的参数
            //-------其他可用的方法，VS的intellisense会告诉您的---------
            IAbacusAirBookLogGenDao abacusAirBookLogGenDao = DALFactory.AbacusAirBookLogGenDao;

            int insertResult = abacusAirBookLogGenDao.InsertAbacusAirBookLogGen(new AbacusAirBookLogGen());

            int updateResult = abacusAirBookLogGenDao.UpdateAbacusAirBookLogGen(new AbacusAirBookLogGen());

            int deleteResult = abacusAirBookLogGenDao.DeleteAbacusAirBookLogGen(new AbacusAirBookLogGen());

            int deleteByFieldResult = abacusAirBookLogGenDao.DeleteAbacusAirBookLogGen(0);


            var resultsByPk = abacusAirBookLogGenDao.FindByPk(0);
            var entities = abacusAirBookLogGenDao.GetAll();

            long count = abacusAirBookLogGenDao.Count();

            var listByPage = abacusAirBookLogGenDao.GetListByPage(null, 0, 0);

        }
    }
}
