using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.orm.attribute
{
    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Struct,
                     Inherited = false, AllowMultiple = false)]
    public class TableAttribute : Attribute
    {
        private string name;
        private string schema;

        public TableAttribute()
            : base()
        { }

        public string Name
        {
            get { return name; }
            set { name = value; }
        }

        public string Schema
        {
            get { return schema; }
            set { schema = value; }
        }
    }
}
