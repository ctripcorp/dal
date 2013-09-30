using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using platform.dao.enums;

namespace platform.dao.log
{
    public interface ILoggerAdapter : IDisposable
    {
        void Init(string name, LogLevel level);

        void Trace(string message);

        void Debug(string message);

        void Info(string message);

        void Warn(string message);

        void Error(string message);

        void Fatal(string message);

    }
}
