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

        /// <summary>
        ///  执行SP${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象</param>
        /// <returns>影响的行数</returns>
        public int Exec${host.getClassName()}(${host.getClassName()} ${WordUtils.uncapitalize(${host.getClassName()})})
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
#foreach($p in $host.getSpParams())
                parameters.Add(new StatementParameter{ Name = "${p.getName()}", Direction = ParameterDirection.${p.getDirection()}, DbType = DbType.${p.getDbType()}, Value = ${WordUtils.uncapitalize(${host.getClassName()})}.${WordUtils.capitalize($p.getName().replace("@",""))}});
#end
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("${host.getSpName()}", parameters);

#foreach ($p in $host.getSpParams())
#if($p.getDirection().name() == "Output" || $p.getDirection().name() == "InputOutput")
                ${WordUtils.uncapitalize(${host.getClassName()})}.${WordUtils.capitalize($p.getName().replace("@",""))} = (${p.getType()})parameters["${p.getName()}"].Value;
#end
#end
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问Exec${host.getClassName()}时出错", ex);
            }

       }
    }
}
