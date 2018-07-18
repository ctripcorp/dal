using Arch.Data.Common.Constant;
using Arch.Data.Common.Util;
using System;

namespace Arch.Data.DbEngine.ConnectionString
{
    class ConnectionLocatorManager
    {
        private static readonly Object obj = new Object();

        private static IConnectionString instance = null;

        public static IConnectionString Instance
        {
            get
            {
                if (instance == null)
                {
                    lock (obj)
                    {
                        if (instance == null)
                        {
                            try
                            {
                                Type locatorType = DALBootstrap.GetConnectionLocatorType();
                                if (locatorType == null)
                                {
                                    Type type = AssemblyUtil.GetTypeFromAssembly(Constants.ArchCtrip, Constants.CtripConnectionString);
                                    if (type != null)
                                        instance = Activator.CreateInstance(type) as IConnectionString;
                                    else
                                        instance = new DefaultConnectionString();
                                }
                                else
                                {
                                    instance = Activator.CreateInstance(locatorType) as IConnectionString;
                                }
                            }
                            catch
                            {
                                instance = new DefaultConnectionString();
                            }
                        }
                    }
                }

                return instance;
            }
        }

    }
}
