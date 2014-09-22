using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.orm.attribute
{
    [AttributeUsage(AttributeTargets.Property | AttributeTargets.Field,
                  Inherited = false, AllowMultiple = false)]
    public class NullableAttribute : Attribute
    {
        public NullableAttribute()  : base()
        { }
    }
}
