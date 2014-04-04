using System;
using System.Collections.Generic;
using com.ctrip.dal.test.test4.Entity.DataModel;

namespace com.ctrip.dal.test.test4.Interface.IDao
{
        public partial interface ITest2Dao
        {


        /// <summary>
        /// 获取所有Test2信息
        /// </summary>
        /// <returns>Test2列表</returns>
        IList<Test2> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索Test2，带翻页
        /// </summary>
        /// <param name="obj">Test2实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<Test2> GetListByPage(Test2 obj, int pagesize, int pageNo);

        }
}