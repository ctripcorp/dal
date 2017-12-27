//${host.getProjectName()}
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using ${host.getNameSpace()}.Entity.DataModel;
using ${host.getNameSpace()}.Interface.IDao;

namespace ${host.getNameSpace()}.Dao
{
    /// <summary>
    ///
    /// </summary>
    public partial class ${host.getClassName()}Dao : I${host.getClassName()}Dao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("${host.getDbSetName()}");
        
#parse("templates/csharp/dao/standard/method.OrmByHand.tpl")
#parse("templates/csharp/dao/standard/method.Insert.sp.tpl")
#parse("templates/csharp/dao/standard/method.Insert.tpl")
#parse("templates/csharp/dao/standard/method.Update.tpl")
#parse("templates/csharp/dao/standard/method.Delete.tpl")
#parse("templates/csharp/dao/standard/method.FindByPk.tpl")
#parse("templates/csharp/dao/standard/method.GetAll.tpl")
#parse("templates/csharp/dao/standard/method.Count.tpl")
#parse("templates/csharp/dao/standard/method.GetListByPage.tpl")
#parse("templates/csharp/dao/standard/method.ToDataTable.tpl")
#parse("templates/csharp/dao/standard/method.BulkInsert.tpl")
#parse("templates/csharp/dao/standard/method.BulkUpdate.tpl")
#parse("templates/csharp/dao/standard/method.BulkDelete.tpl")
#parse("templates/csharp/dao/autosql/DAO.cs.tpl")
        
    }
}