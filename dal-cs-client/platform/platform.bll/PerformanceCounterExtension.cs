using System;
using System.Collections.Generic;
using System.Web;
using System.Diagnostics;
using System.Threading;
using Microsoft.Web.Administration;

namespace platform.bll
{
    public sealed class PerformanceCounterExtension //: IDisposable
    {
        #region Private Var
        private static int processorCount = Environment.ProcessorCount;

        private static Dictionary<int, PerformanceCounter> m_cpuUsageCounters = new Dictionary<int, PerformanceCounter>();

        private static Dictionary<int, PerformanceCounter> totalRequestCounters = new Dictionary<int, PerformanceCounter>();
        private static Dictionary<int, PerformanceCounter> m_SuccessCounters = new Dictionary<int, PerformanceCounter>();
        private static Dictionary<int, PerformanceCounter> m_FailedCounters = new Dictionary<int, PerformanceCounter>();
        private static Dictionary<int, PerformanceCounter> m_401Counters = new Dictionary<int, PerformanceCounter>();
        private static Dictionary<int, PerformanceCounter> m_404Counters = new Dictionary<int, PerformanceCounter>();
        private static Dictionary<int, PerformanceCounter> m_500Counters = new Dictionary<int, PerformanceCounter>();

        private static List<int> ignorePids = new List<int>();
        #endregion

        #region Private Method
        private static string GetProcessInstanceName(int pid)
        {
            string result = string.Empty;
            try
            {
                var category = new PerformanceCounterCategory("Process");

                var instances = category.GetInstanceNames();
                foreach (var instance in instances)
                {

                    using (var counter = new PerformanceCounter(category.CategoryName,
                         "ID Process", instance, true))
                    {
                        int val = (int)counter.RawValue;
                        if (val == pid)
                        {
                            result = instance;
                            break;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                //Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }

            return result;

        }

        #endregion

        #region Public methods

        public static void SynchronizeAllCounters(Dictionary<int, string> poolInstanceName)
        {
            ignorePids.Clear();

            int[] keys = new int[totalRequestCounters.Count];
            totalRequestCounters.Keys.CopyTo(keys, 0);

            for (int i = 0; i < keys.Length; i++)
            {
                totalRequestCounters[keys[i]].Dispose();
                totalRequestCounters[keys[i]] = new PerformanceCounter("ASP.NET Applications", "Requests Total", poolInstanceName[keys[i]]);
            }

            keys = new int[m_SuccessCounters.Count];
            m_SuccessCounters.Keys.CopyTo(keys, 0);

            for (int i = 0; i < keys.Length; i++)
            {
                m_SuccessCounters[keys[i]].Dispose();
            }

            keys = new int[m_FailedCounters.Count];
            m_FailedCounters.Keys.CopyTo(keys, 0);

            for (int i = 0; i < keys.Length; i++)
            {
                m_FailedCounters[keys[i]].Dispose();
            }

            keys = new int[m_401Counters.Count];
            m_401Counters.Keys.CopyTo(keys, 0);

            for (int i = 0; i < keys.Length; i++)
            {
                m_401Counters[keys[i]].Dispose();
            }

            keys = new int[m_404Counters.Count];
            m_404Counters.Keys.CopyTo(keys, 0);

            for (int i = 0; i < keys.Length; i++)
            {
                m_404Counters[keys[i]].Dispose();
            }

            keys = new int[m_500Counters.Count];
            m_500Counters.Keys.CopyTo(keys, 0);

            for (int i = 0; i < keys.Length; i++)
            {
                m_500Counters[keys[i]].Dispose();
            }

            keys = null;
            foreach (var p in poolInstanceName)
            {
                totalRequestCounters[p.Key] = new PerformanceCounter("ASP.NET Applications", "Requests Total", p.Value);
                m_SuccessCounters[p.Key] = new PerformanceCounter("ASP.NET Applications", "Requests Succeeded", p.Value);
                m_FailedCounters[p.Key] = new PerformanceCounter("ASP.NET Applications", "Requests Failed", p.Value);
                m_401Counters[p.Key] = new PerformanceCounter("ASP.NET Applications", "Requests Not Authorized", p.Value);
                m_404Counters[p.Key] = new PerformanceCounter("ASP.NET Applications", "Requests Not Found", p.Value);
                m_500Counters[p.Key] = new PerformanceCounter("ASP.NET Applications", "Requests Timed Out", p.Value);
            }
        }

        public static int GetCpuUsage(int pid)
        {
            if (!m_cpuUsageCounters.ContainsKey(pid))
            {
                m_cpuUsageCounters.Add(pid, new PerformanceCounter("Process", "% Processor Time", GetProcessInstanceName(pid)));
            }

            try
            {
                return (int)(m_cpuUsageCounters[pid].NextValue() / processorCount);
            }
            catch (Exception ex)
            {
                m_cpuUsageCounters[pid].Dispose();
                m_cpuUsageCounters[pid] = null;
                m_cpuUsageCounters.Remove(pid);
                //Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }


            return 0;
        }

        public static int GetTotalRequest(int pid)
        {
            if (!totalRequestCounters.ContainsKey(pid))
            {
                return 0;
            }

            try
            {
                return (int)totalRequestCounters[pid].NextValue();
            }
            catch (Exception ex)
            {
                if (totalRequestCounters.ContainsKey(pid))
                {
                    totalRequestCounters[pid].Dispose();
                    totalRequestCounters[pid] = null;
                    totalRequestCounters.Remove(pid);
                    if (!ignorePids.Contains(pid))
                        ignorePids.Add(pid);
                }
                //Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }

            return 0;

        }

        /// <summary>
        /// 获取200的请求数
        /// </summary>
        /// <param name="pid"></param>
        /// <returns></returns>
        public static int GetSuccessReqeust(int pid)
        {
            if (!m_SuccessCounters.ContainsKey(pid))
            {
                return 0;
            }
            try
            {
                return (int)m_SuccessCounters[pid].NextValue();
            }
            catch (Exception ex)
            {
                m_SuccessCounters[pid].Dispose();
                m_SuccessCounters[pid] = null;
                m_SuccessCounters.Remove(pid);
                if (!ignorePids.Contains(pid))
                    ignorePids.Add(pid);
                //Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }

            return 0;
        }

        /// <summary>
        /// 获取>=400的请求数
        /// </summary>
        /// <param name="pid"></param>
        /// <returns></returns>
        public static int GetFailedReqeust(int pid)
        {
            if (!m_FailedCounters.ContainsKey(pid))
            {
                return 0;
            }

            try
            {
                return (int)m_FailedCounters[pid].NextValue();
            }
            catch (Exception ex)
            {
                m_FailedCounters[pid].Dispose();
                m_FailedCounters[pid] = null;
                m_FailedCounters.Remove(pid);
                if (!ignorePids.Contains(pid))
                    ignorePids.Add(pid);
                //Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }

            return 0;
        }

        /// <summary>
        /// 获取401的请求数
        /// </summary>
        /// <param name="pid"></param>
        /// <returns></returns>
        public static int GetNotAuthorizedReqeust(int pid)
        {
            if (!m_401Counters.ContainsKey(pid))
            {
                return 0;
            }

            try
            {
                return (int)m_401Counters[pid].NextValue();
            }
            catch (Exception ex)
            {
                m_401Counters[pid].Dispose();
                m_401Counters[pid] = null;
                m_401Counters.Remove(pid);
                if (!ignorePids.Contains(pid))
                    ignorePids.Add(pid);
                //Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }
            return 0;
        }

        /// <summary>
        /// 获取404的请求数
        /// </summary>
        /// <param name="pid"></param>
        /// <returns></returns>
        public static int GetNotFoundReqeust(int pid)
        {
            if (!m_404Counters.ContainsKey(pid))
            {
                return 0;
            }

            try
            {
                return (int)m_404Counters[pid].NextValue();
            }
            catch (Exception ex)
            {
                m_404Counters[pid].Dispose();
                m_404Counters[pid] = null;
                m_404Counters.Remove(pid);
                if (!ignorePids.Contains(pid))
                    ignorePids.Add(pid);
                //Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }

            return 0;
        }

        /// <summary>
        /// 获取500的请求数
        /// </summary>
        /// <param name="pid"></param>
        /// <returns></returns>
        public static int GetServerErrorReqeust(int pid)
        {
            if (!m_500Counters.ContainsKey(pid))
            {
                return 0;
            }

            try
            {
                return (int)m_500Counters[pid].NextValue();
            }
            catch (Exception ex)
            {
                m_500Counters[pid].Dispose();
                m_500Counters[pid] = null;
                m_500Counters.Remove(pid);
                if (!ignorePids.Contains(pid))
                    ignorePids.Add(pid);
                //Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }
            return 0;
        }

        #endregion
    }
}