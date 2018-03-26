using System;
using System.Collections.Generic;
using ${host.getNameSpace()}.Entity.DataModel;

namespace ${host.getNameSpace()}.Interface.IDao
{
        public partial interface I${host.getClassName()}Dao
        {

#if($host.isTable())
#if($host.generateAPI(1))
        /// <summary>
        /// 手工映射，建议使用1.2.0.5版本以上的VisitDataReader
        /// </summary>
        /// <returns>结果</returns>
        //${host.getClassName()} OrmByHand(string sql);
#end
#if($host.generateAPI(46,53))
        /// <summary>
        ///  插入${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象</param>
#if($host.isSpa())
        /// <returns>状态代码</returns>
		int Insert${host.getClassName()}(${host.getClassName()} ${WordUtils.uncapitalize(${host.getClassName()})});
#else
        /// <returns>新增的主键,如果有多个主键则返回第一个主键</returns>
#set($returnType=[])
#foreach($p in $host.getColumns())
#if($p.isIdentity() || $p.isPrimary())
#set($success = $returnType.add(${p.getType()}))
#end
#end
#if($returnType.size()<1)
	#set($success = $returnType.add("int"))
#end
		$returnType.get(0) Insert${host.getClassName()}(${host.getClassName()} ${WordUtils.uncapitalize($host.getClassName())});
#end   
#end
#if($host.getPrimaryKeys().size() == 0)
        /*由于没有PK，不能生成Update和Delete方法
#end
#if($host.generateAPI(47,55))
        /// <summary>
        /// 修改${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象</param>
        /// <returns>状态代码</returns>
        int Update${host.getClassName()}(${host.getClassName()} ${WordUtils.uncapitalize(${host.getClassName()})});
#end
#if($host.generateAPI(48,57,58,59))
        /// <summary>
        /// 删除${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象</param>
        /// <returns>状态代码</returns>
        int Delete${host.getClassName()}(${host.getClassName()} ${WordUtils.uncapitalize(${host.getClassName()})});
#end
#if($host.isSpa())
#if($host.getSpaDelete().isExist())
#if($host.generateAPI(48,57,58))
        /// <summary>
        /// 删除${host.getClassName()}
        /// </summary>
#foreach ($p in $host.getSpaDelete().getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName().replace('@', ''))}">${WordUtils.uncapitalize($p.getName())} #></param>
#end
        /// <returns>状态代码</returns>
        int Delete${host.getClassName()}(#foreach ($p in $host.getSpaDelete().getParameters())${p.getType()} ${WordUtils.uncapitalize($p.getName().replace("@",""))}#if($foreach.count != $host.getSpaDelete().getParameters().size()),#end#end);
#end
#end
#end
#if($host.getPrimaryKeys().size() == 0)
        */
#end
#if($host.generateAPI(42,49))
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
#end
#if($host.generateAPI(43,50))
        /// <summary>
        /// 获取所有${host.getClassName()}信息
        /// </summary>
        /// <returns>${host.getClassName()}列表</returns>
        IList<${host.getClassName()}> GetAll();
#end
#if($host.generateAPI(44,51))
        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();
#end
#if($host.generateAPI(54))
        /// <summary>
        ///  批量插入${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象列表</param>
        /// <returns>状态代码</returns>
        bool BulkInsert${host.getClassName()}(IList<${host.getClassName()}> ${WordUtils.uncapitalize(${host.getClassName()})}List);
#end
#if($host.generateAPI(74))
#if($host.getDatabaseCategory().name() == "MySql")
		/// <summary>
        ///  批量插入${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象列表</param>
        /// <returns>状态代码</returns>
        bool BulkInsert${host.getClassName()}(IList<${host.getClassName()}> ${WordUtils.uncapitalize(${host.getClassName()})}List);
#end
#end
#if($host.generateAPI(56))
#if($host.isHasSptU())
        /// <summary>
        ///  批量更新${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象列表</param>
        /// <returns>状态代码</returns>
        int BulkUpdate${host.getClassName()}(IList<${host.getClassName()}> ${WordUtils.uncapitalize(${host.getClassName()})}List);
#end
#end
#if($host.generateAPI(59))
#if($host.isHasSptD())
        /// <summary>
        ///  批量删除${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象列表</param>
        /// <returns>状态代码</returns>
        int BulkDelete${host.getClassName()}(IList<${host.getClassName()}> ${WordUtils.uncapitalize(${host.getClassName()})}List);
#end
#end
#if($host.generateAPI(45,52))
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
#end
#parse("templates/csharp/dao/autosql/IDAO.cs.tpl")

        }
}