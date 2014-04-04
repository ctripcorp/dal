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
    public class FltOrdersTmpTest
    {
        public static void Test()
        {
            //以下方法的主要目的是教会您如何使用DAL
            //在实际使用的时候，您需要根据不同的情形
            //反注释相应的代码，并传入合法的参数
            //-------其他可用的方法，VS的intellisense会告诉您的---------
            IFltOrdersTmpDao fltOrdersTmpDao = DALFactory.FltOrdersTmpDao;

            int insertResult = fltOrdersTmpDao.InsertFltOrdersTmp(new FltOrdersTmp());

            int updateResult = fltOrdersTmpDao.UpdateFltOrdersTmp(new FltOrdersTmp());

            int deleteResult = fltOrdersTmpDao.DeleteFltOrdersTmp(new FltOrdersTmp());

            int deleteByFieldResult = fltOrdersTmpDao.DeleteFltOrdersTmp(0);


            var resultsByPk = fltOrdersTmpDao.FindByPk(0);
            var entities = fltOrdersTmpDao.GetAll();

            long count = fltOrdersTmpDao.Count();

            var listByPage = fltOrdersTmpDao.GetListByPage(null, 0, 0);

        }
    }
}
