using System;

namespace Arch.Data.Common.Vi
{
    class AbstractHABean : IHABean
    {
        private AbstractHABean() { }
        private static IHABean bean = null;
        private static readonly Object beanLock = new Object();

        public static void Register()
        {
            if (bean == null)
            {
                lock (beanLock)
                {
                    if (bean == null)
                    {
                        bean = new AbstractHABean();
                    }
                }
            }
        }

        public static IHABean GetInstance()
        {
            return bean;
        }

        private volatile Boolean _enableHA;
        private volatile Int32 _retryTimes;
        private volatile String _sqlServerErrorCodes;
        private volatile String _mySqlErrorCodes;

        public Boolean EnableHA
        {
            get { return _enableHA; }
            set { _enableHA = value; }
        }

        public Int32 RetryTimes
        {
            get { return _retryTimes; }
            set { _retryTimes = value; }
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
