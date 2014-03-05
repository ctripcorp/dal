using System;
using System.Collections.Generic;
using com.ctrip.platform.tools.Entity.DataModel;

namespace using com.ctrip.platform.tools.IDao
{
	public partial interface ISDP_Version_genDao
	{

	       /// <summary>
        ///  插入SDP_Version_gen
        /// </summary>
        /// <param name="sDP_Version_gen">SDP_Version_gen实体对象</param>
        /// <returns>新增的主键</returns>
        int InsertSDP_Version_gen(SDP_Version_gen sDP_Version_gen);

        /// <summary>
        /// 修改SDP_Version_gen
        /// </summary>
        /// <param name="sDP_Version_gen">SDP_Version_gen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateSDP_Version_gen(SDP_Version_gen sDP_Version_gen);

        /// <summary>
        /// 删除SDP_Version_gen
        /// </summary>
        /// <param name="sDP_Version_gen">SDP_Version_gen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteSDP_Version_gen(SDP_Version_gen sDP_Version_gen);


        /// <summary>
        /// 根据主键获取SDP_Version_gen信息
        /// </summary>
        /// <param name="iD"></param>
        /// <returns>SDP_Version_gen信息</returns>
        SDP_Version_gen FindByPk(long iD);

        /// <summary>
        /// 获取所有SDP_Version_gen信息
        /// </summary>
        /// <returns>SDP_Version_gen列表</returns>
        IList<SDP_Version_gen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();


        /// <summary>
        ///  GetTest
        /// </summary>
        /// <param name="iD"></param>
        /// <returns></returns>
        IList<SDP_Version_gen> GetTest(long iD);
	}
}