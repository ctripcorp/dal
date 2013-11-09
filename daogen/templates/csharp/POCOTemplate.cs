using System;
using platform.dao.orm.attribute;

namespace {{dao.namespace}}
{
    [Serializable]
    [Table(Name="{{dao.table_name}}")]
    public class {{dao.class_name}}
    {
        {% for field in dao.fields %}
        /// <summary>
        /// {{field.name}}
        /// </summary>
        [Column(Name="{{field.name}}"){% if field.is_primary %}, PrimaryKey{% end %}]
        public {{field.ftype}}{% if field.value_type %}?{% end %} {{field.name}} { get; set; }
        {% end %}
    }
}