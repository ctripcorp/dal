using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using platform.dao.enums;

namespace platform.dao.log
{
    public class AbstractLoggerAdapter : ILogAdapter
    {
        protected string name;
        protected LogLevel level;



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

        /// <summary>
        /// 日志名称，级别初始化日志
        /// </summary>
        /// <param name="name"></param>
        /// <param name="level"></param>
        public virtual void Init(string name, LogLevel level)
        {
            this.name = name;
            this.level = level;
        }

        protected string FormatLogMessage(string level, string message)
        {
            return string.Format("[{0}]--[{1}]--[{2}]--{3}", level, name, DateTime.Now, message);
        }


        public virtual void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        /// <summary>
        /// 真正的资源释放程序
        /// </summary>
        /// <param name="isDisposing">是否正在释放</param>
        protected virtual void Dispose(bool isDisposing)
        {
        }

    }
}
