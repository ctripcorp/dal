using Arch.Data.Common.Logging;
using System;

namespace Arch.Data.Common.Vi
{
    class AbstractDALBean : IDALBean
    {
        private AbstractDALBean() { }
        private static IDALBean bean = null;
        private static readonly Object beanLock = new Object();

        public static void Register()
        {
            if (bean == null)
            {
                lock (beanLock)
                {
                    if (bean == null)
                    {
                        bean = new AbstractDALBean();
                    }
                }
            }
        }

        public static IDALBean GetInstance()
        {
            return bean;
        }

        private volatile Boolean _logListenersTurnOn;
        private volatile Boolean _tracingTurnOn;
        private volatile LogLevel _centralLoggingLevel;
        private volatile Int32 _logConcurrentCapacity;
        private volatile Int32 _metricsAggeragationDuration;
        private volatile Int32 _slaveDelayReadDuration;
        private volatile Int32 _commandTimeout = 0;

        public Boolean LogListenersTurnOn
        {
            get { return _logListenersTurnOn; }
            set { _logListenersTurnOn = value; }
        }

        public Boolean TracingTurnOn
        {
            get { return _tracingTurnOn; }
            set { _tracingTurnOn = value; }
        }

        public LogLevel CentralLoggingLevel
        {
            get { return _centralLoggingLevel; }
            set { _centralLoggingLevel = value; }
        }

        public Int32 LogConcurrentCapacity
        {
            get { return _logConcurrentCapacity; }
            set { if (value > 0) _logConcurrentCapacity = value; }
        }

        public Int32 MetricsAggeragationDuration
        {
            get { return _metricsAggeragationDuration; }
            set { if (value > 0)  _metricsAggeragationDuration = value; }
        }

        public Int32 SlaveDelayReadDuration
        {
            get { return _slaveDelayReadDuration; }
            set { if (value > 0)  _slaveDelayReadDuration = value; }
        }

        public Int32 CommandTimeout
        {
            get { return _commandTimeout; }
            set { if (value > 0)  _commandTimeout = value; }
        }

    }
}