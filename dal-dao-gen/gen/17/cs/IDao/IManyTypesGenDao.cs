using System;
using System.Collections.Generic;
using com.ctrip.shard.Entity.DataModel;

namespace com.ctrip.shard.Interface.IDao
{
	public partial interface IManyTypesGenDao
	{

	       /// <summary>
        ///  插入ManyTypesGen
        /// </summary>
        /// <param name="manyTypesGen">ManyTypesGen实体对象</param>
        /// <returns>新增的主键</returns>
        int InsertManyTypesGen(ManyTypesGen manyTypesGen);

        /// <summary>
        /// 修改ManyTypesGen
        /// </summary>
        /// <param name="manyTypesGen">ManyTypesGen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateManyTypesGen(ManyTypesGen manyTypesGen);

        /// <summary>
        /// 删除ManyTypesGen
        /// </summary>
        /// <param name="manyTypesGen">ManyTypesGen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteManyTypesGen(ManyTypesGen manyTypesGen);


        /// <summary>
        /// 根据主键获取ManyTypesGen信息
        /// </summary>
        /// <param name="id"></param>
        /// <returns>ManyTypesGen信息</returns>
        ManyTypesGen FindByPk(int id);

        /// <summary>
        /// 获取所有ManyTypesGen信息
        /// </summary>
        /// <returns>ManyTypesGen列表</returns>
        IList<ManyTypesGen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索ManyTypesGen，带翻页
        /// </summary>
        /// <param name="obj">ManyTypesGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<ManyTypesGen> GetListByPage(ManyTypesGen obj, int pagesize, int pageNo);

	}
}