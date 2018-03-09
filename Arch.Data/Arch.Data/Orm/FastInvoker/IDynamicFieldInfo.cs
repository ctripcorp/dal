using System;

namespace Arch.Data.Orm.FastInvoker
{
    public interface IDynamicFieldInfo
    {
        Object GetValue(Object obj);

        void SetValue(Object obj, Object value);
    }
}
