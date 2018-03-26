using Arch.Data.Common.Logging;
using System;

namespace Arch.Data.DbEngine.Executor
{
    public interface IExecutor
    {
        void Daemon();

        void Dispose();

        Boolean EnqueueLogEntry(ILogEntry entry);

        Boolean EnqueueCallback(Action callback);

        void StartRWCallback(Action callback, Int32 delay);

    }
}