using System;
using System.Collections.Generic;
using com.ctrip.platform.tools.Entity.DataModel;

namespace com.ctrip.platform.tools.Interface.IDao
{
	public partial interface ISDP_SH_PriceBatch_genDao
	{

	       /// <summary>
        ///  插入SDP_SH_PriceBatch_gen
        /// </summary>
        /// <param name="sDP_SH_PriceBatch_gen">SDP_SH_PriceBatch_gen实体对象</param>
        /// <returns>新增的主键</returns>
        int InsertSDP_SH_PriceBatch_gen(SDP_SH_PriceBatch_gen sDP_SH_PriceBatch_gen);

        /// <summary>
        /// 修改SDP_SH_PriceBatch_gen
        /// </summary>
        /// <param name="sDP_SH_PriceBatch_gen">SDP_SH_PriceBatch_gen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateSDP_SH_PriceBatch_gen(SDP_SH_PriceBatch_gen sDP_SH_PriceBatch_gen);

        /// <summary>
        /// 删除SDP_SH_PriceBatch_gen
        /// </summary>
        /// <param name="sDP_SH_PriceBatch_gen">SDP_SH_PriceBatch_gen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteSDP_SH_PriceBatch_gen(SDP_SH_PriceBatch_gen sDP_SH_PriceBatch_gen);


        /// <summary>
        /// 根据主键获取SDP_SH_PriceBatch_gen信息
        /// </summary>
        /// <param name="iD"></param>
        /// <returns>SDP_SH_PriceBatch_gen信息</returns>
        SDP_SH_PriceBatch_gen FindByPk(long iD);

        /// <summary>
        /// 获取所有SDP_SH_PriceBatch_gen信息
        /// </summary>
        /// <returns>SDP_SH_PriceBatch_gen列表</returns>
        IList<SDP_SH_PriceBatch_gen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();


        /// <summary>
        ///  getAllByID
        /// </summary>
        /// <param name="iD"></param>
        /// <returns></returns>
        public IList<SDP_SH_PriceBatch_gen> getAllByID(long iD);
	}
}