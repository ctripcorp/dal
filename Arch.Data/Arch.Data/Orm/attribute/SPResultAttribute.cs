using System;

namespace Arch.Data.Orm
{
    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Struct, Inherited = false)]
    public class SPResultAttribute : Attribute { }
}
