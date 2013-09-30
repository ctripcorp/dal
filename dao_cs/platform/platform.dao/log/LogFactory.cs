using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using platform.dao.enums;

namespace platform.dao.log
{
    public sealed class LogFactory
    {

        public static ILoggerAdapter GetLogger(string name, LogLevel level=LogLevel.Debug)
        {
            //return ConsoleLoggerAdapter.GetInstance(name, level);
            return FileLoggerAdapter.GetInstance(name, level);
        }

    }
}
