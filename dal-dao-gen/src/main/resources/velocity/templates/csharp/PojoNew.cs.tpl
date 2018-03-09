using System;
using System.Data;
using Arch.Data.Orm;

namespace ${host.getNameSpace()}.Entity.DataModel
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
#if($column.getComment()!="")
		/// $!column.getComment()
#end
        /// </summary>
#if($column.isDataChangeLastTimeField())
        [Ignored]
#end
        [Column(Name = "${column.getName()}",ColumnType=DbType.${column.getDbType()}#if($column.getLength() > 0),Length=${column.getLength()}#end)#if($column.isIdentity()),ID#end#if($column.isPrimary()),PK#end]
        public ${column.getType()}#if($column.isNullable() && $column.isValueType() && $column.getType() != "string")?#end #if($WordUtils.capitalize($column.getName()) == $host.getClassName())${host.getClassName()}_Gen#{else}${WordUtils.capitalize($column.getName())}#end { get; set; }
#end
    }
}