using Arch.Data.Common.Constant;
using Arch.Data.Common.Util;
using System;

namespace Arch.Data.Common.Vi
{
    public static class BeanManager
    {
        private static IBeanProxy proxy = null;
        private static readonly Object obj = new Object();

        public static void Register()
        {
            if (proxy == null)
            {
                lock (obj)
                {
                    if (proxy == null)
                    {
                        try
                        {
                            Type type = AssemblyUtil.GetTypeFromAssembly(Constants.ArchCtrip, Constants.CtripBeanProxy);
                            proxy = type != null ?
                                Activator.CreateInstance(type) as IBeanProxy : new BeanProxy();
                        }
                        catch
                        {
                            proxy = new BeanProxy();
                        }
                    }
                }
            }

            proxy.Register();
        }

        public static IDALBean GetDALBean()
        {
            return proxy.GetDALBean();
        }

        public static IHABean GetHABean()
        {
            return proxy.GetHABean();
        }

        public static IMarkDownBean GetMarkDownBean()
        {
            return proxy.GetMarkDownBean();
        }

        public static ITimeoutMarkDownBean GetTimeoutMarkDownBean()
        {
            return proxy.GetTimeoutBean();
        }

    }
}
