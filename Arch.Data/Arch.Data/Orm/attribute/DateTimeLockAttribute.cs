using System;

namespace Arch.Data.Orm
{
    [AttributeUsage(AttributeTargets.Field | AttributeTargets.Property)]
    public class DateTimeLockAttribute : Attribute
    {
        public String Expression { get; set; }
    }
}
