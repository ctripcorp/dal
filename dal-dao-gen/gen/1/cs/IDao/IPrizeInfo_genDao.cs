using System;
using System.Collections.Generic;
using com.ctrip.platform.tools.Entity.DataModel;

namespace com.ctrip.platform.tools.Interface.IDao
{
	public partial interface IPrizeInfo_genDao
	{

	       /// <summary>
        ///  插入PrizeInfo_gen
        /// </summary>
        /// <param name="prizeInfo_gen">PrizeInfo_gen实体对象</param>
        /// <returns>状态代码</returns>
        int InsertPrizeInfo_gen(PrizeInfo_gen prizeInfo_gen);

        /// <summary>
        /// 修改PrizeInfo_gen
        /// </summary>
        /// <param name="prizeInfo_gen">PrizeInfo_gen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdatePrizeInfo_gen(PrizeInfo_gen prizeInfo_gen);

        /// <summary>
        /// 删除PrizeInfo_gen
        /// </summary>
        /// <param name="prizeInfo_gen">PrizeInfo_gen实体对象</param>
        /// <returns>状态代码</returns>
        int DeletePrizeInfo_gen(PrizeInfo_gen prizeInfo_gen);


        /// <summary>
        /// 根据主键获取PrizeInfo_gen信息
        /// </summary>
        /// <param name="prizeInfoID"></param>
        /// <returns>PrizeInfo_gen信息</returns>
        PrizeInfo_gen FindByPk(int prizeInfoID);

        /// <summary>
        /// 获取所有PrizeInfo_gen信息
        /// </summary>
        /// <returns>PrizeInfo_gen列表</returns>
        IList<PrizeInfo_gen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索PrizeInfo_gen，带翻页
        /// </summary>
        /// <param name="obj">PrizeInfo_gen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<PrizeInfo_gen> GetListByPage(PrizeInfo_gen obj, int pagesize, int pageNo);

	}
}