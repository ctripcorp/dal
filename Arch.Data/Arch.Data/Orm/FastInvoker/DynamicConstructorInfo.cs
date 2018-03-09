using System;
using System.Collections.Concurrent;
using System.Reflection;

namespace Arch.Data.Orm.FastInvoker
{
    internal delegate Object DynamicConstructorInfoHandler(Object[] parameters);

    class DynamicConstructorInfo : IDynamicConstructorInfo
    {
        Type type;
        ConstructorInfo info;
        DynamicConstructorInfoHandler handler;

        public DynamicConstructorInfo(Type type, ConstructorInfo info)
        {
            this.type = type;
            this.info = info;
        }

        public Object Invoke(Object[] parameters)
        {
            if (handler != null) return handler(parameters);

            Int32 moduleKey = info.Module.GetHashCode();
            Int32 handlerKey = info.MetadataToken;
            handler = DynamicCacheFactory<DynamicConstructorInfoHandler>.Caches
                .GetOrAdd(moduleKey, innerModuleKey => new ConcurrentDictionary<Int32, DynamicConstructorInfoHandler>())
                .GetOrAdd(handlerKey, (Int32 innerHandlerKey) => DynamicMethodFactory.CreateDynamicConstructorInfoHandler(type, info));

            return handler(parameters);
        }

    }
}
