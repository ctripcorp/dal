using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using ${host.getNameSpace()}.Entity.DataModel;
using ${host.getNameSpace()}.Dao;

namespace ${host.getNameSpace()}.Test
{
    public class ${host.getClassName()}Test
    {
        public static void Test()
        {
        	${host.getClassName()}Dao ${WordUtils.uncapitalize($host.getClassName())}Dao = DALFactory.${host.getClassName()}Dao;
#foreach($method in $host.getMethods())
            //IList<${method.getPojoName()}> ${method.getName()}Result = ${WordUtils.uncapitalize($host.getClassName())}Dao.${method.getName()}(#foreach($p in $method.getParameters())${p.getValue()}#if($foreach.count != $method.getParameters().size()),#end#end);

#end
        }
    }
}