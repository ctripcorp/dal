using System;

namespace Arch.Data.Orm
{
    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Struct, Inherited = false)]
    public class TableAttribute : Attribute
    {
        public String Name { get; set; }

        public String Schema { get; set; }
    }
}
