using System;

namespace Arch.Data.Common.Vi
{
    class AbstractTimeoutMarkDownBean : ITimeoutMarkDownBean
    {
        private AbstractTimeoutMarkDownBean() { }
        private static ITimeoutMarkDownBean bean = null;
        private static readonly Object beanLock = new Object();

        public static void Register()
        {
            if (bean == null)
            {
                lock (beanLock)
                {
                    if (bean == null)
                    {
                        bean = new AbstractTimeoutMarkDownBean();
                    }
                }
            }
        }

        public static ITimeoutMarkDownBean GetInstance()
        {
            return bean;
        }

        private volatile Boolean _enableTimeoutMarkDown;
        private volatile Int32 _samplingDuration;
        private volatile Int32 _errorCountThreshold;
        private volatile Int32 _errorPercentReferCount;
        private Double _errorPercentThreshold;
        private volatile String _mySqlErrorCodes;
        private volatile String _sqlServerErrorCodes;

        public Boolean EnableTimeoutMarkDown
        {
            get { return _enableTimeoutMarkDown; }
            set { _enableTimeoutMarkDown = value; }
        }

        public Int32 SamplingDuration
        {
            get { return _samplingDuration; }
            set { if (value > 0) _samplingDuration = value; }
        }

        public Int32 ErrorPercentReferCount
        {
            get { return _errorPercentReferCount; }
            set { _errorPercentReferCount = value; }
        }

        public Int32 ErrorCountThreshold
        {
            get { return _errorCountThreshold; }
            set { if (value > 0)  _errorCountThreshold = value; }
        }

        public Double ErrorPercentThreshold
        {
            get { return _errorPercentThreshold; }
            set { if (value > 0) _errorPercentThreshold = value; }
        }

        public String SqlServerErrorCodes
        {
            get { return _sqlServerErrorCodes; }
            set { _sqlServerErrorCodes = value; }
        }

        public String MySqlErrorCodes
        {
            get { return _mySqlErrorCodes; }
            set { _mySqlErrorCodes = value; }
        }

    }
}
