using System;
using System.Collections.Generic;
using com.ctrip.platform.tools.Entity.DataModel;

namespace com.ctrip.platform.tools.Interface.IDao
{
	public partial interface IPeople_view_genDao
	{


        /// <summary>
        /// 获取所有People_view_gen信息
        /// </summary>
        /// <returns>People_view_gen列表</returns>
        IList<People_view_gen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索People_view_gen，带翻页
        /// </summary>
        /// <param name="obj">People_view_gen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<People_view_gen> GetListByPage(People_view_gen obj, int pagesize, int pageNo);

	}
}