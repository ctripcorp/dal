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
        [Column(Name="{{field.name}}"), PrimaryKey]
        public {{field.type}} {{field.label}} { get; set; }

        {% end %}

    }
}