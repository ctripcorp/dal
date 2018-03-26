using System;

namespace Arch.Data.Common.Vi
{
    class AbstractMarkDownBean : IMarkDownBean
    {
        private AbstractMarkDownBean() { }
        private static IMarkDownBean bean = null;
        private static readonly Object beanLock = new Object();

        public static void Register()
        {
            if (bean == null)
            {
                lock (beanLock)
                {
                    if (bean == null)
                    {
                        bean = new AbstractMarkDownBean();
                    }
                }
            }
        }

        public static IMarkDownBean GetInstance()
        {
            return bean;
        }

        private volatile Boolean _enableAutoMarkDown;
        private volatile Boolean _appIsMarkDown;
        private volatile Int32 _autoMarkUpDelay;
        private volatile Int32 _autoMarkUpBatches;
        private volatile String _autoMarkUpSchedule;
        private volatile String _allInOneKeys;

        public Boolean EnableAutoMarkDown
        {
            get { return _enableAutoMarkDown; }
            set { _enableAutoMarkDown = value; }
        }

        public Boolean AppIsMarkDown
        {
            get { return _appIsMarkDown; }
            set { _appIsMarkDown = value; }
        }

        public Int32 AutoMarkUpDelay
        {
            get { return _autoMarkUpDelay; }
            set { if (value > 0)  _autoMarkUpDelay = value; }
        }

        public Int32 AutoMarkUpBatches
        {
            get { return _autoMarkUpBatches; }
            set { _autoMarkUpBatches = value; }
        }

        public String AutoMarkUpSchedule
        {
            get { return _autoMarkUpSchedule; }
            set { _autoMarkUpSchedule = value; }
        }

        public String MarkDownKeys
        {
            get;
            set;
        }

        public String AutoMarkDowns
        {
            get;
            private set;
        }

        public String AllInOneKeys
        {
            get { return _allInOneKeys; }
            set { _allInOneKeys = value; }
        }

    }
}
