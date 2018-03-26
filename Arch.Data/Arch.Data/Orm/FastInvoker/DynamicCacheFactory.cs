using System;
using System.Collections.Concurrent;

namespace Arch.Data.Orm.FastInvoker
{
    class DynamicCacheFactory<T>
    {
        private static ConcurrentDictionary<Int32, ConcurrentDictionary<Int32, T>> caches = new ConcurrentDictionary<Int32, ConcurrentDictionary<Int32, T>>();

        public static ConcurrentDictionary<Int32, ConcurrentDictionary<Int32, T>> Caches
        {
            get { return caches; }
        }
    }

}
