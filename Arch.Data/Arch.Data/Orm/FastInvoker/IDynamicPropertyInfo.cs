using System;

namespace Arch.Data.Orm.FastInvoker
{
    public interface IDynamicPropertyInfo
    {
        Object GetValue(Object obj, Object[] index);

        void SetValue(Object obj, Object value, Object[] index);
    }
}
