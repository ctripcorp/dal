using Arch.Data.Common.Constant;
using Arch.Data.DbEngine;
using Arch.Data.DbEngine.Executor;
using System;

namespace Arch.Data.Common.Util
{
    public class ExecutorManager
    {
        private static readonly Object obj = new Object();
        private static IExecutor executor = null;

        public static IExecutor Executor
        {
            get
            {
                if (executor == null)
                {
                    lock (obj)
                    {
                        if (executor == null)
                        {
                            try
                            {
                                Type executorType = DALBootstrap.GetExecutorType();
                                if (executorType == null)
                                {
                                    Type type = AssemblyUtil.GetTypeFromAssembly(Constants.ArchCtrip, Constants.CtripExecutor);
                                    if (type != null)
                                        executor = Activator.CreateInstance(type) as IExecutor;
                                    else
                                        executor = new DefaultExecutor();
                                }
                                else
                                {
                                    executor = Activator.CreateInstance(executorType) as IExecutor;
                                }
                            }
                            catch
                            {
                                executor = new DefaultExecutor();
                            }
                        }
                    }
                }

                return executor;
            }
        }
    }
}