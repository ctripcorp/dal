using System;
using System.Collections.Generic;
using System.Threading;
using System.Web;

namespace Arch.Release.ServerAgent.Client.XMonCollector
{
    public class CTimer
    {
        static volatile CTimerInternal _instance = null;
        private static readonly object LockObj = new object();

        public static void Run(Action callback, object state, int dueTime, int period)
        {
            if (_instance != null)
                return;
            lock (LockObj)
            {
                if (_instance != null)
                    return;
                _instance = new CTimerInternal(callback, state, dueTime, period);
                AppDomain.CurrentDomain.DomainUnload += new EventHandler(_instance.Dispose);
            }

        }

        public static void Run(Action callback, int period)
        {
            Run(callback, null, 0, period);
        }

        class CTimerInternal : IDisposable
        {
            // 1 if timer callback is executing; otherwise 0
            private int _inTimerCallback = 0;

            private Timer _timer = null;

            private readonly Action _callback = null;

            public CTimerInternal(Action callback, object state, int dueTime, int period)
            {
                _callback = callback;
                this._timer = new Timer(TimerCallback, state, dueTime, period);
            }

            //
            // if the timer fires frequently or the callback runs for a long period, 
            // you may want to prevent two threads from calling it concurrently
            //
            private void TimerCallback(object state)
            {
                // if the callback is already being executed, just return
                if (Interlocked.Exchange(ref _inTimerCallback, 1) != 0)
                {
                    return;
                }
                try
                {
                    // do work (potentially long running work that 
                    // may call into native code)
                    _callback();
                }
                finally
                {
                    Interlocked.Exchange(ref _inTimerCallback, 0);
                }
            }

            public void Dispose(object o, EventArgs args)
            {
                Dispose();
            }

            //
            // Before the AppDomain is shutdown, the timer must be disposed.  Otherwise,
            // the underlying native timer may crash the process if it fires and attempts
            // to call into the unloaded AppDomain.  In a multi-threaded environment,
            // you may need to use synchronization to ensure the timer is disposed at 
            // most once.
            //
            public void Dispose()
            {
                Timer timer = _timer;
                if (timer != null
                    && Interlocked.CompareExchange(ref _timer, null, timer) == timer)
                {
                    timer.Dispose();
                }


                // if you don’t want the timer callback to be aborted during an 
                // AppDomain unload, or if it calls into native code, then loop until 
                // the callback has completed
                while (_inTimerCallback != 0)
                {
                    Thread.Sleep(100);
                }
            }
        }
    }
}