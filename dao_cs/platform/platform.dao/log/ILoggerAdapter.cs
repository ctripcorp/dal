using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.log
{
    public interface ILoggerAdapter
    {
        void Trace(string message);

        void Debug(string message);

        void Info(string message);

        void Warn(string message);

        void Error(string message);

        void Fatal(string message);

    }
}
