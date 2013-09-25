using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.log
{
    public sealed class LogFactory
    {

        public static ILoggerAdapter GetLogger(string name)
        {
            return new ConsoleLoggerAdapter();
        }

    }
}
