using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;

namespace ${host.getNamespaceDao()}
{
    class Program
    {
        static void Main(string[] args)
        {
#foreach($daoHost in ${host.getDaoHosts()})
			I${daoHost.getClassName()}Dao ${WordUtils.uncapitalize($daoHost.getClassName())} = DALFactory.${daoHost.getClassName()}Dao;
#foreach($method in $daoHost.getMethods())
			${WordUtils.uncapitalize($daoHost.getClassName())}.${method.getName()}();
#end
#end
        }
    }
}
