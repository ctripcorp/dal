using Arch.Data.Common.Constant;
using Arch.Data.DbEngine;
using Arch.Data.DbEngine.RW;
using System;

namespace Arch.Data.Common.Util
{
    public class RWSplittingManager
    {
        private static readonly Object obj = new Object();

        private static IReadWriteSplitting instance = null;

        public static IReadWriteSplitting Instance
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
                                Type rwType = DALBootstrap.GetRWSplittingType();
                                if (rwType == null)
                                {
                                    Type type = AssemblyUtil.GetTypeFromAssembly(Constants.ArchCtrip, Constants.RWSplitting);
                                    if (type != null)
                                        instance = Activator.CreateInstance(type) as IReadWriteSplitting;
                                    else
                                        instance = new DefaultReadWriteSplitting();
                                }
                                else
                                {
                                    instance = Activator.CreateInstance(rwType) as IReadWriteSplitting;
                                }
                            }
                            catch
                            {
                                instance = new DefaultReadWriteSplitting();
                            }
                        }
                    }
                }

                return instance;
            }
        }

    }
}