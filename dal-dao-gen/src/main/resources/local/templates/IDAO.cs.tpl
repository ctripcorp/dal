using System;
using System.Collections.Generic;
using ${host.getNameSpace()}.Entity.DataModel;

namespace ${host.getNameSpace()}.Interface.IDao
{
	public partial interface I${host.getClassName()}Dao
	{

#if($host.isTable())
	       /// <summary>
        ///  插入${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象</param>
#if($host.isSpa())
        /// <returns>状态代码</returns>
#else
        /// <returns>新增的主键</returns>
#end
        int Insert${host.getClassName()}(${host.getClassName()} ${WordUtils.uncapitalize(${host.getClassName()})});

#if($host.getPrimaryKeys().size() == 0)
        /*由于没有PK，不能生成Update和Delete方法
#end
        /// <summary>
        /// 修改${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象</param>
        /// <returns>状态代码</returns>
        int Update${host.getClassName()}(${host.getClassName()} ${WordUtils.uncapitalize(${host.getClassName()})});

        /// <summary>
        /// 删除${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象</param>
        /// <returns>状态代码</returns>
        int Delete${host.getClassName()}(${host.getClassName()} ${WordUtils.uncapitalize(${host.getClassName()})});

#if($host.isSpa())
#if($host.getSpaDelete().exists())
        /// <summary>
        /// 删除${host.getClassName()}
        /// </summary>
#foreach ($p in $host.getSpaDelete().getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName().replace('@', ''))}">${WordUtils.uncapitalize($p.getName())} #></param>
#end
        /// <returns>状态代码</returns>
        int Delete${host.getClassName()}(#foreach ($p in $host.getSpaDelete().getParameters()))${p.getType()} ${WordUtils.uncapitalize($p.getName().replace("@",""))}#if($foreach.count != $host.getDeleteParameterList().size()),#end#end);
#end
#end
#if($host.getPrimaryKeys().size() == 0)
        */
#end

#if($host.getPrimaryKeys().size() >= 1)
        /// <summary>
        /// 根据主键获取${host.getClassName()}信息
        /// </summary>
#foreach ($cpk in $host.getPrimaryKeys())
        /// <param name="${WordUtils.uncapitalize($cpk.getName())}"></param>
#end
        /// <returns>${host.getClassName()}信息</returns>
        ${host.getClassName()} FindByPk(#foreach ($cpk in $host.getPrimaryKeys())${cpk.getType()} ${WordUtils.uncapitalize($cpk.getName())}#if($foreach.count != $host.getPrimaryKeys().size()),#end#end);
#end
#end

        /// <summary>
        /// 获取所有${host.getClassName()}信息
        /// </summary>
        /// <returns>${host.getClassName()}列表</returns>
        IList<${host.getClassName()}> GetAll();

#if($host.isHasSptI())
        /// <summary>
        ///  批量插入${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象列表</param>
        /// <returns>状态代码</returns>
        int BulkInsertPeople(IList<${host.getClassName()}> ${WordUtils.uncapitalize(${host.getClassName()})}List);
#end

#if($host.isHasSptU())
        /// <summary>
        ///  批量更新${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象列表</param>
        /// <returns>状态代码</returns>
        int BulkUpdatePeople(IList<${host.getClassName()}> ${WordUtils.uncapitalize(${host.getClassName()})}List);
#end

#if($host.isHasSptD())
        /// <summary>
        ///  批量删除${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象列表</param>
        /// <returns>状态代码</returns>
        int BulkDeletePeople(IList<${host.getClassName()}> ${WordUtils.uncapitalize(${host.getClassName()})}List);
#end

        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

#if($host.isHasPagination())
#if($host.getDatabaseCategory().name() == "MySql" )
        /// <summary>
        ///  检索${host.getClassName()}，带翻页
        /// </summary>
        /// <param name="obj">${host.getClassName()}实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<${host.getClassName()}> GetListByPage(${host.getClassName()} obj, int pagesize, int pageNo);
#else
        /// <summary>
        ///  检索${host.getClassName()}，带翻页
        /// </summary>
        /// <param name="obj">${host.getClassName()}实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<${host.getClassName()}> GetListByPage(${host.getClassName()} obj, int pagesize, int pageNo);
#end
#end

#foreach($method in $host.getExtraMethods())
        /// <summary>
        ///  ${method.getName()}
        /// </summary>
#foreach($p in $method.getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName())}"></param>
#end
        /// <returns></returns>
        #if($method.getCrud_type() == "select")IList<${host.getClassName()}>#{else}int#end ${method.getName()}(#foreach($p in $method.getParameters())${p.getType()} ${WordUtils.uncapitalize($p.getName())}#if($foreach.count != $method.getParameters().size()),#end#end);
#end
	}
}