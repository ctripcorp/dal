using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.log
{
    public class AbstractLoggerAdapter : ILoggerAdapter
    {
        public virtual void Trace(string message)
        {
            throw new NotImplementedException();
        }

        public virtual void Debug(string message)
        {
            throw new NotImplementedException();
        }

        public virtual void Info(string message)
        {
            throw new NotImplementedException();
        }

        public virtual void Warn(string message)
        {
            throw new NotImplementedException();
        }

        public virtual void Error(string message)
        {
            throw new NotImplementedException();
        }

        public virtual void Fatal(string message)
        {
            throw new NotImplementedException();
        }
    }
}
