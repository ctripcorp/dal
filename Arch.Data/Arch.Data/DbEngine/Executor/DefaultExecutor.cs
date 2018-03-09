using Arch.Data.Common.Logging;
using System;

namespace Arch.Data.DbEngine.Executor
{
    public class DefaultExecutor : IExecutor
    {
        public void Daemon() { }

        public void Dispose() { }

        public Boolean EnqueueLogEntry(ILogEntry entry)
        {
            return false;
        }

        public Boolean EnqueueCallback(Action callback)
        {
            return false;
        }

        public void StartRWCallback(Action callback, Int32 delay) { }

    }
}
