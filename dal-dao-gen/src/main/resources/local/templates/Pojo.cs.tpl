using System;
using Arch.Data.Orm;

namespace ${host.getNameSpaceDao()}
{
    /// <summary>
    /// ${host.getTableName()}
    /// </summary>
    [Serializable]
    [Table(Name = "${host.getTableName()}")]
    public partial class ${host.getClassName()}
    {
#foreach($column in $host.getColumns())
        /// <summary>
        /// 获取或设置${column.getName()}
        /// </summary>
        [Column(Name = "${column.getName()}")#if($column.isIdentity()),ID#end#if($column.isPrimary()),PK#end]
        public ${column.getType()}#if($column.isNullable())?#end #if($WordUtils.capitalizeFully($column.getName()) == $host.getClassName())${host.getClassName()}_Gen#{else}${WordUtils.capitalizeFully($column.getName())}#end { get; set; }
#end
    }
}