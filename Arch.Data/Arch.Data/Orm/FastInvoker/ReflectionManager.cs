using System;
using System.Reflection;

namespace Arch.Data.Orm.FastInvoker
{
    public class ReflectionManager
    {
        public static IDynamicType CreateDynamicType(Type type)
        {
            return new DynamicType(type);
        }

        public static IDynamicPropertyInfo CreateDynamicProperty(PropertyInfo info)
        {
            return new DynamicPropertyInfo(info.DeclaringType, info);
        }

        public static IDynamicFieldInfo CreateDynamicFieldInfo(FieldInfo info)
        {
            return new DynamicFieldInfo(info.DeclaringType, info);
        }
    }
}
