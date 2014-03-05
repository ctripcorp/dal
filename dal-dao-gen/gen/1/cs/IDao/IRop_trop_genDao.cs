using System;
using System.Collections.Generic;
using com.ctrip.platform.tools.Entity.DataModel;

namespace using com.ctrip.platform.tools.IDao
{
	public partial interface IRop_trop_genDao
	{

	       /// <summary>
        ///  插入Rop_trop_gen
        /// </summary>
        /// <param name="rop_trop_gen">Rop_trop_gen实体对象</param>
        /// <returns>新增的主键</returns>
        int InsertRop_trop_gen(Rop_trop_gen rop_trop_gen);

        /// <summary>
        /// 修改Rop_trop_gen
        /// </summary>
        /// <param name="rop_trop_gen">Rop_trop_gen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateRop_trop_gen(Rop_trop_gen rop_trop_gen);

        /// <summary>
        /// 删除Rop_trop_gen
        /// </summary>
        /// <param name="rop_trop_gen">Rop_trop_gen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteRop_trop_gen(Rop_trop_gen rop_trop_gen);


        /// <summary>
        /// 根据主键获取Rop_trop_gen信息
        /// </summary>
        /// <param name="id"></param>
        /// <returns>Rop_trop_gen信息</returns>
        Rop_trop_gen FindByPk(int id);

        /// <summary>
        /// 获取所有Rop_trop_gen信息
        /// </summary>
        /// <returns>Rop_trop_gen列表</returns>
        IList<Rop_trop_gen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();


        /// <summary>
        ///  tableTest
        /// </summary>
        /// <param name="createdtime"></param>
        /// <returns></returns>
        IList<Rop_trop_gen> tableTest(DateTime createdtime);
	}
}