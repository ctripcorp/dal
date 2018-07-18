using System;

namespace Arch.Data.Orm
{
    [AttributeUsage(AttributeTargets.Property | AttributeTargets.Field)]
    public class IgnoredAttribute : Attribute { }
}