using System;
using Arch.Data.Orm;

namespace DAL.Entity.DataModel
{
    /// <summary>
    /// ${host.getTableName()}
    /// </summary>
    [Serializable]
    [Table(Name = "${host.getTableName()}")]
    public partial class JustQuery
    {
    }
}