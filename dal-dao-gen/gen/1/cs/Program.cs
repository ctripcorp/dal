using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.platform.tools;
using com.ctrip.platform.toolsIDao;

namespace com.ctrip.platform.toolsDao
{
    class Program
    {
        static void Main(string[] args)
        {
        //以下方法的主要目的是教会您如何使用DAL
        //在实际使用的时候，您需要根据不同的情形
        //反注释相应的代码，并传入合法的参数
        //-------其他可用的方法，VS的intellisense会告诉您的---------
			IPrizeInfoDao prizeInfoDao = DALFactory.PrizeInfoDao;

			//int result = prizeInfoDao.InsertPrizeInfo(new PrizeInfo());
			//int result = prizeInfoDao.UpdatePrizeInfo(new PrizeInfo());
			//int result = prizeInfoDao.DeletePrizeInfo(new PrizeInfo());
			//PrizeInfo entity = prizeInfoDao.FindByPk(id);
			//IList<PrizeInfo> entities = prizeInfoDao.GetAll();
			//long count = prizeInfoDao.Count();
			//IList<PrizeInfo> listByPage = prizeInfoDao.GetListByPage(obj, pagesize, pageno);
        }
    }
}
