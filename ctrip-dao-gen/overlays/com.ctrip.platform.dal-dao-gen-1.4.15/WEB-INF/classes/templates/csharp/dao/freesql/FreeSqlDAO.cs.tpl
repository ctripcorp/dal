//${host.getProjectName()}
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

#parse("templates/csharp/dao/freesql/method.scalar.FirstOrSingle.tpl")
#parse("templates/csharp/dao/freesql/method.scalar.List.tpl")
#parse("templates/csharp/dao/freesql/method.cud.tpl")

    }
}