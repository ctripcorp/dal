using System;
using platform.dao.orm.attribute;

namespace $namespace
{
    [Serializable]
    [Table(Name="$table_name")]
    public class $pojo_name
    {
        #foreach( $field in $fields )
        /// <summary>
        /// ${field.getName()}
        /// </summary>
        [Column(Name="${field.getName()}")#if( ${field.isPrimary()} ), PrimaryKey#end]
        public ${CSharpSqlTypeMap.get($field.getType())}#if( $field.isNullable() )?#end ${field.getName()} { get; set; }
        #end
    }
}