using System;

namespace Arch.Data.Orm
{
    [AttributeUsage(AttributeTargets.Field | AttributeTargets.Property)]
    public class RetrunValueAttribute : Attribute { }
}
