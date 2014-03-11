using System;
using System.Collections.Generic;
using com.ctrip.shard.Entity.DataModel;

namespace com.ctrip.shard.Interface.IDao
{
	public partial interface IDjangoAdminLogGenDao
	{

	       /// <summary>
        ///  插入DjangoAdminLogGen
        /// </summary>
        /// <param name="djangoAdminLogGen">DjangoAdminLogGen实体对象</param>
        /// <returns>新增的主键</returns>
        int InsertDjangoAdminLogGen(DjangoAdminLogGen djangoAdminLogGen);

        /// <summary>
        /// 修改DjangoAdminLogGen
        /// </summary>
        /// <param name="djangoAdminLogGen">DjangoAdminLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateDjangoAdminLogGen(DjangoAdminLogGen djangoAdminLogGen);

        /// <summary>
        /// 删除DjangoAdminLogGen
        /// </summary>
        /// <param name="djangoAdminLogGen">DjangoAdminLogGen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteDjangoAdminLogGen(DjangoAdminLogGen djangoAdminLogGen);


        /// <summary>
        /// 根据主键获取DjangoAdminLogGen信息
        /// </summary>
        /// <param name="id"></param>
        /// <returns>DjangoAdminLogGen信息</returns>
        DjangoAdminLogGen FindByPk(int id);

        /// <summary>
        /// 获取所有DjangoAdminLogGen信息
        /// </summary>
        /// <returns>DjangoAdminLogGen列表</returns>
        IList<DjangoAdminLogGen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();


        /// <summary>
        ///  deletey
        /// </summary>
        /// <param name="action_flag"></param>
        /// <param name="id"></param>
        /// <returns></returns>
        int deletey(short action_flag,int id);
	}
}