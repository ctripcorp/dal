using System;
using System.Collections.Concurrent;
using System.Reflection;

namespace Arch.Data.Orm.FastInvoker
{
    internal delegate Object DynamicFieldGetHandler(Object obj);
    internal delegate void DynamicFieldSetHandler(Object obj, Object value);

    class DynamicFieldInfo : IDynamicFieldInfo
    {
        Type type;
        FieldInfo info;
        DynamicFieldSetHandler setHandler;
        DynamicFieldGetHandler getHandler;

        public DynamicFieldInfo(Type type, FieldInfo info)
        {
            this.type = type;
            this.info = info;
        }

        public Object GetValue(Object obj)
        {
            if (getHandler != null) return getHandler(obj);

            Int32 moduleKey = info.Module.GetHashCode();
            Int32 handlerKey = info.MetadataToken;

            getHandler = DynamicCacheFactory<DynamicFieldGetHandler>.Caches
                .GetOrAdd(moduleKey, innerModuleKey => new ConcurrentDictionary<Int32, DynamicFieldGetHandler>())
                .GetOrAdd(handlerKey, (Int32 innerHandlerKey) => DynamicMethodFactory.CreateGetHandler(type, info));

            return getHandler(obj);
        }

        public void SetValue(Object obj, Object value)
        {
            if (setHandler != null)
            {
                setHandler(obj, value);
                return;
            }

            Int32 moduleKey = info.Module.GetHashCode();
            Int32 handlerKey = info.MetadataToken;

            setHandler = DynamicCacheFactory<DynamicFieldSetHandler>.Caches
                .GetOrAdd(moduleKey, innerModuleKey => new ConcurrentDictionary<Int32, DynamicFieldSetHandler>())
                .GetOrAdd(handlerKey, innerHandlerKey => DynamicMethodFactory.CreateSetHandler(type, info));

            setHandler(obj, value);
        }
    }
}
