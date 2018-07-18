using Arch.Data.Common.Constant;
using Arch.Data.Common.Logging;
using Arch.Data.DbEngine;
using System;

namespace Arch.Data.Common.Util
{
    public class LogManager
    {
        private static readonly Object obj = new Object();
        private static ILogger logger = null;

        public static ILogger Logger
        {
            get
            {
                if (logger == null)
                {
                    lock (obj)
                    {
                        if (logger == null)
                        {
                            try
                            {
                                Type loggingType = DALBootstrap.GetLoggingType();
                                if (loggingType == null)
                                {
                                    Type type = AssemblyUtil.GetTypeFromAssembly(Constants.ArchCtrip, Constants.CtripLogger);
                                    if (type != null)
                                        logger = Activator.CreateInstance(type) as ILogger;
                                    else
                                        logger = new DefaultLogger();
                                }
                                else
                                {
                                    logger = Activator.CreateInstance(loggingType) as ILogger;
                                }
                            }
                            catch
                            {
                                logger = new DefaultLogger();
                            }
                        }
                    }
                }

                return logger;
            }
        }

    }
}