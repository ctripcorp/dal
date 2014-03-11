using System;
using System.Collections.Generic;
using DAL.Entity.DataModel;

namespace DAL.Interface.IDao
{
	public partial interface IJustQueryDao
	{


        /// <summary>
        /// 获取所有JustQuery信息
        /// </summary>
        /// <returns>JustQuery列表</returns>
        IList<JustQuery> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();


	}
}