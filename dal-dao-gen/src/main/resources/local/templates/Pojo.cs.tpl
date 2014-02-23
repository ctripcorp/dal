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
        [Column(Name = "${column.getName()}"#if($column.getLength() > 0),Length=${column.getLength()}#end)#if($column.isIdentity()),ID#end#if($column.isPrimary()),PK#end]
        public ${column.getType()}#if($column.isNullable())?#end #if($WordUtils.capitalize($column.getName()) == $host.getClassName())${host.getClassName()}_Gen#{else}${WordUtils.capitalize($column.getName())}#end { get; set; }
#end
    }
}