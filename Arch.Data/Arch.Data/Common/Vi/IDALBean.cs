using Arch.Data.Common.Logging;
using System;

namespace Arch.Data.Common.Vi
{
    public interface IDALBean
    {
        Boolean LogListenersTurnOn { get; set; }

        Boolean TracingTurnOn { get; set; }

        LogLevel CentralLoggingLevel { get; set; }

        Int32 LogConcurrentCapacity { get; set; }

        Int32 MetricsAggeragationDuration { get; set; }

        Int32 SlaveDelayReadDuration { get; set; }

        Int32 CommandTimeout { get; set; }

    }
}
