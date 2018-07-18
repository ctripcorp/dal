using System;
using System.Collections.Concurrent;
using System.Reflection;

namespace Arch.Data.Orm.FastInvoker
{
    internal delegate void DynamicPropertySetHandler(Object obj, Object value, Object[] index);
    internal delegate Object DynamicPropertyGetHandler(Object obj, Object[] index);

    class DynamicPropertyInfo : IDynamicPropertyInfo
    {
        Type type;
        PropertyInfo info;
        DynamicPropertySetHandler setHandler;
        DynamicPropertyGetHandler getHandler;

        public DynamicPropertyInfo(Type type, PropertyInfo info)
        {
            this.type = type;
            this.info = info;
        }

        /// <summary>
        /// 获取属性值
        /// </summary>
        /// <param name="obj">对象</param>
        /// <param name="index">检索</param>
        /// <returns></returns>
        public Object GetValue(Object obj, Object[] index)
        {
            if (getHandler != null) return getHandler(obj, index);

            Int32 moduleKey = info.Module.GetHashCode();
            Int32 handlerKey = info.MetadataToken;

            getHandler = DynamicCacheFactory<DynamicPropertyGetHandler>.Caches
                .GetOrAdd(moduleKey, innerModuleKey => new ConcurrentDictionary<Int32, DynamicPropertyGetHandler>())
                .GetOrAdd(handlerKey, innerHandlerKey => DynamicMethodFactory.CreateGetHandler(type, info));

            return getHandler(obj, index);
        }

        public void SetValue(Object obj, Object value, Object[] index)
        {
            if (setHandler != null)
            {
                setHandler(obj, value, index);
                return;
            }

            Int32 moduleKey = info.Module.GetHashCode();
            Int32 handlerKey = info.MetadataToken;

            setHandler = DynamicCacheFactory<DynamicPropertySetHandler>.Caches
                .GetOrAdd(moduleKey, innerModuleKey => new ConcurrentDictionary<Int32, DynamicPropertySetHandler>())
                .GetOrAdd(handlerKey, innerHandlerKey => DynamicMethodFactory.CreateSetHandler(type, info));

            setHandler(obj, value, index);
        }

    }
}
