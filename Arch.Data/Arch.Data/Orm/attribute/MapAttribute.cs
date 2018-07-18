using System;

namespace Arch.Data.Orm
{
    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Struct, AllowMultiple = true, Inherited = false)]
    public class MapAttribute : ColumnAttribute
    {
        public MapAttribute(String field)
        {
            Field = field;
        }

        public String Field { get; private set; }

        public Boolean PK { get; set; }

        public Boolean ID { get; set; }

        public Boolean OutPut { get; set; }

        public Boolean ReturnValue { get; set; }

    }
}
