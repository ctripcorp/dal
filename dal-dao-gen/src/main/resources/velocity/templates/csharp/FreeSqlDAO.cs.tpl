using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using ${host.getNameSpace()}.Entity.DataModel;

namespace ${host.getNameSpace()}.Dao
{
    public partial class ${host.getClassName()}Dao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("${host.getDbSetName()}");

#foreach($method in $host.getMethods())
#if($method.isScalar())
		public object ${method.getName()} (#foreach($p in $method.getParameters())#if($p.isInParameter())List<${p.getType()}>#{else}${p.getType()}#end ${WordUtils.uncapitalize($p.getName())}#if($foreach.count != $method.getParameters().size()),#end#end)
		{
			try
			{
				string sql = "${method.getSql()}";
                StatementParameterCollection parameters = new StatementParameterCollection();
				#set($inParams = [])                
#foreach($p in $method.getParameters())  
#if($p.isInParameter())
#set($success = $inParams.add($p))
#else
                parameters.Add(new StatementParameter{ Name = "@${p.getName()}", Direction = ParameterDirection.Input, DbType = DbType.${p.getDbType()}, Value =${WordUtils.uncapitalize($p.getName())} });
#end
#end
#if($inParams.size() > 0)
                sql = string.Format(sql, #foreach($p in $inParams)Arch.Data.Utility.ParameterUtility.NormalizeInParam(${WordUtils.uncapitalize($p.getAlias())}, parameters,"${WordUtils.uncapitalize($p.getAlias())}")#if($foreach.count != $inParams.size()),#end#end);
#end
	            return baseDao.ExecScalar(sql, parameters);
			}catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问${method.getName()}时出错", ex);
            }
		}
#else
	
		/// <summary>
        ///  ${method.getName()}
        /// </summary>
#foreach($p in $method.getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName())}"></param>
#end
        /// <returns></returns>
        public IList<${method.getPojoName()}> ${method.getName()}(#foreach($p in $method.getParameters())#if($p.isInParameter())List<${p.getType()}>#{else}${p.getType()}#end ${WordUtils.uncapitalize($p.getName())}#if($foreach.count != $method.getParameters().size()),#end#end)
        {
        	try
            {
            	string sql = "${method.getSql()}";
                StatementParameterCollection parameters = new StatementParameterCollection();
#set($inParams = [])                
#foreach($p in $method.getParameters())  
#if($p.isInParameter())
#set($success = $inParams.add($p))
#else
                parameters.Add(new StatementParameter{ Name = "@${p.getName()}", Direction = ParameterDirection.Input, DbType = DbType.${p.getDbType()}, Value =${WordUtils.uncapitalize($p.getName())} });
#end
#end
#if($inParams.size() > 0)
                sql = string.Format(sql, #foreach($p in $inParams)Arch.Data.Utility.ParameterUtility.NormalizeInParam(${WordUtils.uncapitalize($p.getAlias())}, parameters,"${WordUtils.uncapitalize($p.getAlias())}")#if($foreach.count != $inParams.size()),#end#end);
#end
				//如果只需要一条记录，建议使用limit 1或者top 1，并使用SelectFirst提高性能
				//return baseDao.SelectFirst<${method.getPojoName()}>(sql, parameters);
                return baseDao.SelectList<${method.getPojoName()}>(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问${method.getName()}时出错", ex);
            }
        }
#end
#end

    }
}