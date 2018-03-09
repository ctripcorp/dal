using Arch.Data.Common.Enums;
using Arch.Data.DbEngine;
using Arch.Data.DbEngine.MarkDown;
using System;
using System.Configuration;
using System.Diagnostics;

namespace Arch.Data.Common.Logging
{
    class DefaultLogger : ILogger
    {
        public void Prepare(String logName, ConfigurationElementCollection collection, out LogLevel level)
        {
            level = LogLevel.Information;
        }

        public void Init(ILogEntry entry, Statement statement, String databaseName, String message) { }

        public void Start(ILogEntry entry, Stopwatch watch) { }

        public void Success(ILogEntry entry, Statement statement, Stopwatch watch, Func<Int32> func) { }

        public void Error(Exception ex, ILogEntry entry, Statement statement, Stopwatch watch) { }

        public void Complete(ILogEntry entry) { }

        public void Log(ILogEntry entry) { }

        public void LogMarkdown(LogLevel level, String message, String errorCode) { }

        public void StartTracing() { }

        public void StopTracing() { }

        public void Next() { }

        public void TracingError(Exception ex) { }

        public void MetricsLog(String databaseSet, DatabaseType dbType, OperationType optType) { }

        public void MetricsMarkdown(MarkDownMetrics metrics) { }

        public void MetricsMarkup(MarkUpMetrics metrics) { }

        public void MetricsRW(String databaseSet, String databaseName, Boolean success) { }

        public void MetricsFailover(String databaseSet, String allInOneKey) { }

    }
}
