using System;
using System.Collections.Generic;
using com.ctrip.platform.tools.Entity.DataModel;

namespace using com.ctrip.platform.tools.IDao
{
	public partial interface ISysdiagrams_genDao
	{

	       /// <summary>
        ///  插入Sysdiagrams_gen
        /// </summary>
        /// <param name="sysdiagrams_gen">Sysdiagrams_gen实体对象</param>
        /// <returns>状态代码</returns>
        int InsertSysdiagrams_gen(Sysdiagrams_gen sysdiagrams_gen);

        /// <summary>
        /// 修改Sysdiagrams_gen
        /// </summary>
        /// <param name="sysdiagrams_gen">Sysdiagrams_gen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateSysdiagrams_gen(Sysdiagrams_gen sysdiagrams_gen);

        /// <summary>
        /// 删除Sysdiagrams_gen
        /// </summary>
        /// <param name="sysdiagrams_gen">Sysdiagrams_gen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteSysdiagrams_gen(Sysdiagrams_gen sysdiagrams_gen);


        /// <summary>
        /// 根据主键获取Sysdiagrams_gen信息
        /// </summary>
        /// <param name="diagram_id"></param>
        /// <returns>Sysdiagrams_gen信息</returns>
        Sysdiagrams_gen FindByPk(int diagram_id);

        /// <summary>
        /// 获取所有Sysdiagrams_gen信息
        /// </summary>
        /// <returns>Sysdiagrams_gen列表</returns>
        IList<Sysdiagrams_gen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索Sysdiagrams_gen，带翻页
        /// </summary>
        /// <param name="obj">Sysdiagrams_gen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<Sysdiagrams_gen> GetListByPage(Sysdiagrams_gen obj, int pagesize, int pageNo);

	}
}