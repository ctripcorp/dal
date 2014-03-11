using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.shard.Entity.DataModel;
using com.ctrip.shard.Interface.IDao;
using com.ctrip.shard.Dao;

namespace com.ctrip.shard.Test
{
    public class ManyTypesGenTest
    {
        public static void Test()
        {
            //以下方法的主要目的是教会您如何使用DAL
            //在实际使用的时候，您需要根据不同的情形
            //反注释相应的代码，并传入合法的参数
            //-------其他可用的方法，VS的intellisense会告诉您的---------
            IManyTypesGenDao manyTypesGenDao = DALFactory.ManyTypesGenDao;

            int insertResult = manyTypesGenDao.InsertManyTypesGen(new ManyTypesGen());

            int updateResult = manyTypesGenDao.UpdateManyTypesGen(new ManyTypesGen());

            int deleteResult = manyTypesGenDao.DeleteManyTypesGen(new ManyTypesGen());

            var resultsByPk = FindByPk(null);

            var entities = manyTypesGenDao.GetAll();

            long count = manyTypesGenDao.Count();

            var listByPage = manyTypesGenDao.GetListByPage(null, 0, 0);

        }
    }
}
