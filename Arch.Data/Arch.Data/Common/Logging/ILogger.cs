using Arch.Data.Common.Enums;
using Arch.Data.DbEngine;
using Arch.Data.DbEngine.MarkDown;
using System;
using System.Configuration;
using System.Diagnostics;

namespace Arch.Data.Common.Logging
{
    public interface ILogger
    {
        #region CLog

        void Prepare(String logName, ConfigurationElementCollection collection, out LogLevel level);

        void Init(ILogEntry entry, Statement statement, String databaseName, String message);

        void Start(ILogEntry entry, Stopwatch watch);

        void Success(ILogEntry entry, Statement statement, Stopwatch watch, Func<Int32> func);

        void Error(Exception ex, ILogEntry entry, Statement statement, Stopwatch watch);

        void Complete(ILogEntry entry);

        void Log(ILogEntry entry);

        void LogMarkdown(LogLevel level, String message, String errorCode);

        #endregion

        #region Tracing

        void StartTracing();

        void StopTracing();

        void Next();

        void TracingError(Exception ex);

        #endregion

        #region Metrics

        void MetricsLog(String databaseSet, DatabaseType dbType, OperationType optType);

        void MetricsMarkdown(MarkDownMetrics metrics);

        void MetricsMarkup(MarkUpMetrics metrics);

        void MetricsRW(String databaseSet, String databaseName, Boolean success);

        void MetricsFailover(String databaseSet, String allInOneKey);

        #endregion
    }
}
