using System;
using System.Collections.Generic;
using com.ctrip.platform.uat.Entity.DataModel;

namespace com.ctrip.platform.uat.Interface.IDao
{
	public partial interface IPeople_view_genDao
	{

	       /// <summary>
        ///  插入People_view_gen
        /// </summary>
        /// <param name="people_view_gen">People_view_gen实体对象</param>
        /// <returns>状态代码</returns>
        int InsertPeople_view_gen(People_view_gen people_view_gen);

        /*由于没有PK，不能生成Update和Delete方法
        /// <summary>
        /// 修改People_view_gen
        /// </summary>
        /// <param name="people_view_gen">People_view_gen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdatePeople_view_gen(People_view_gen people_view_gen);

        /// <summary>
        /// 删除People_view_gen
        /// </summary>
        /// <param name="people_view_gen">People_view_gen实体对象</param>
        /// <returns>状态代码</returns>
        int DeletePeople_view_gen(People_view_gen people_view_gen);

        */


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

	}
}