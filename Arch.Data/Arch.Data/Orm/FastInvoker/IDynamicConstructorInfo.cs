using System;

namespace Arch.Data.Orm.FastInvoker
{
    public interface IDynamicConstructorInfo
    {
        Object Invoke(Object[] parameters);
    }
}
