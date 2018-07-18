using Arch.Data.DbEngine.Providers;
using System;

namespace Arch.Data.Common.Cat
{
    public interface ICatLogger
    {
        void AddData();

        void LogEvent(String methodName, IDatabaseProvider databaseProvider, String connectionString);

        void SetStatus();

        void LogError(Exception ex);

        void Complete();

        void LogRecordCount(String name, Int64 count);
    }
}
