using System;
using System.Collections.Generic;
using System.Web;
using System.Diagnostics;
using System.Threading;
using Microsoft.Web.Administration;

namespace TestAgent
{
    public sealed class PerformanceCounterExtension //: IDisposable
    {

        //private static PerformanceCounterExtension _instance = new PerformanceCounterExtension();

        //private PerformanceCounterExtension()
        //{

        //}

        //public static PerformanceCounterExtension Instance { get { return _instance; } }

        #region Private Var
        private static int processorCount = Environment.ProcessorCount;

        private static Dictionary<int, PerformanceCounter> m_cpuUsageCounters = new Dictionary<int, PerformanceCounter>();
        private static Dictionary<int, int> m_CpuUsages = new Dictionary<int, int>();

        private static Dictionary<int, PerformanceCounter> totalRequestCounters = new Dictionary<int, PerformanceCounter>();
        private static Dictionary<int, PerformanceCounter> m_SuccessCounters = new Dictionary<int, PerformanceCounter>();
        private static Dictionary<int, PerformanceCounter> m_FailedCounters = new Dictionary<int, PerformanceCounter>();
        private static Dictionary<int, PerformanceCounter> m_401Counters = new Dictionary<int, PerformanceCounter>();
        private static Dictionary<int, PerformanceCounter> m_404Counters = new Dictionary<int, PerformanceCounter>();
        private static Dictionary<int, PerformanceCounter> m_500Counters = new Dictionary<int, PerformanceCounter>();

        private static Dictionary<int, int> totalRequestCount = new Dictionary<int, int>();
        private static Dictionary<int, float> lastSuccess = new Dictionary<int, float>();
        private static Dictionary<int, float> lastFailed = new Dictionary<int, float>();
        private static Dictionary<int, float> last401 = new Dictionary<int, float>();
        private static Dictionary<int, float> last404 = new Dictionary<int, float>();
        private static Dictionary<int, float> last500 = new Dictionary<int, float>();

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

        #region CreateAllRequestPerformanceCounter
        private static void CreateAllRequestPerformanceCounter()
        {
            using (ServerManager serverManager = new ServerManager())
            {

                foreach (var w in serverManager.WorkerProcesses)
                {
                    if (ignorePids.Contains(w.ProcessId))
                    {
                        continue;
                    }
                    foreach (var a in w.ApplicationDomains)
                    {
                        var counter_id = a.Id.Replace("/", "_");
                        if (!totalRequestCounters.ContainsKey(w.ProcessId))
                        {
                            try
                            {
                                totalRequestCounters.Add(w.ProcessId, new PerformanceCounter("ASP.NET Applications", "Requests Total", counter_id));
                            }
                            catch
                            { //TODO: at least do something here
                            }
                        }
                        if (!m_SuccessCounters.ContainsKey(w.ProcessId))
                        {
                            try
                            {
                                m_SuccessCounters.Add(w.ProcessId, new PerformanceCounter("ASP.NET Applications", "Requests Succeeded", counter_id));
                            }
                            catch
                            { //TODO: at least do something here
                            }
                        }
                        if (!m_FailedCounters.ContainsKey(w.ProcessId))
                        {
                            try
                            {
                                m_FailedCounters.Add(w.ProcessId, new PerformanceCounter("ASP.NET Applications", "Requests Failed", counter_id));
                            }
                            catch
                            { //TODO: at least do something here
                            }
                        }
                        if (!m_401Counters.ContainsKey(w.ProcessId))
                        {
                            try
                            {
                                m_401Counters.Add(w.ProcessId, new PerformanceCounter("ASP.NET Applications", "Requests Not Authorized", counter_id));
                            }
                            catch
                            { //TODO: at least do something here
                            }
                        }
                        if (!m_404Counters.ContainsKey(w.ProcessId))
                        {
                            try
                            {
                                m_404Counters.Add(w.ProcessId, new PerformanceCounter("ASP.NET Applications", "Requests Not Found", counter_id));
                            }
                            catch
                            { //TODO: at least do something here
                            }
                        }
                        if (!m_500Counters.ContainsKey(w.ProcessId))
                        {
                            try
                            {
                                m_500Counters.Add(w.ProcessId, new PerformanceCounter("ASP.NET Applications", "Requests Timed Out", counter_id));
                            }
                            catch
                            { //TODO: at least do something here
                            }
                        }
                    }
                }
            }
        }
        #endregion

        #endregion

        #region Public methods

        public static void SynchronizeAllCounters(Dictionary<int, string> w3wp)
        {
            //ignorePids.Clear();

            int[] keys = new int[m_cpuUsageCounters.Count];
            m_cpuUsageCounters.Keys.CopyTo(keys, 0);

            for (int i = 0; i < keys.Length; i++)
            {
                if (!w3wp.ContainsKey(keys[i]))
                {
                    m_cpuUsageCounters[keys[i]].Dispose();
                    m_cpuUsageCounters[keys[i]] = null;
                    m_cpuUsageCounters.Remove(keys[i]);
                    m_CpuUsages.Remove(keys[i]);
                }
            }

            keys = new int[totalRequestCounters.Count];
            totalRequestCounters.Keys.CopyTo(keys, 0);

            for (int i = 0; i < keys.Length; i++)
            {
                if (!w3wp.ContainsKey(keys[i]))
                {
                    totalRequestCounters[keys[i]].Dispose();
                    totalRequestCounters[keys[i]] = null;
                    totalRequestCounters.Remove(keys[i]);
                    totalRequestCount.Remove(keys[i]);
                }
            }

            keys = new int[m_SuccessCounters.Count];
            m_SuccessCounters.Keys.CopyTo(keys, 0);

            for (int i = 0; i < keys.Length; i++)
            {
                if (!w3wp.ContainsKey(keys[i]))
                {
                    m_SuccessCounters[keys[i]].Dispose();
                    m_SuccessCounters[keys[i]] = null;
                    m_SuccessCounters.Remove(keys[i]);
                    lastSuccess.Remove(keys[i]);
                }
            }

            keys = new int[m_FailedCounters.Count];
            m_FailedCounters.Keys.CopyTo(keys, 0);

            for (int i = 0; i < keys.Length; i++)
            {
                if (!w3wp.ContainsKey(keys[i]))
                {
                    m_FailedCounters[keys[i]].Dispose();
                    m_FailedCounters[keys[i]] = null;
                    m_FailedCounters.Remove(keys[i]);
                    lastFailed.Remove(keys[i]);
                }
            }

            keys = new int[m_401Counters.Count];
            m_401Counters.Keys.CopyTo(keys, 0);

            for (int i = 0; i < keys.Length; i++)
            {
                if (!w3wp.ContainsKey(keys[i]))
                {
                    m_401Counters[keys[i]].Dispose();
                    m_401Counters[keys[i]] = null;
                    m_401Counters.Remove(keys[i]);
                    last401.Remove(keys[i]);
                }
            }

            keys = new int[m_404Counters.Count];
            m_404Counters.Keys.CopyTo(keys, 0);

            for (int i = 0; i < keys.Length; i++)
            {
                if (!w3wp.ContainsKey(keys[i]))
                {
                    m_404Counters[keys[i]].Dispose();
                    m_404Counters[keys[i]] = null;
                    m_404Counters.Remove(keys[i]);
                    last404.Remove(keys[i]);
                }
            }

            keys = new int[m_500Counters.Count];
            m_500Counters.Keys.CopyTo(keys, 0);

            for (int i = 0; i < keys.Length; i++)
            {
                if (!w3wp.ContainsKey(keys[i]))
                {
                    m_500Counters[keys[i]].Dispose();
                    m_500Counters[keys[i]] = null;
                    m_500Counters.Remove(keys[i]);
                    last500.Remove(keys[i]);
                }
            }

            keys = null;
        }

        public static int GetCpuUsage(int pid)
        {
            if (!m_cpuUsageCounters.ContainsKey(pid))
            {
                //Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Debug("Only Once?");
                m_cpuUsageCounters.Add(pid, new PerformanceCounter("Process", "% Processor Time", GetProcessInstanceName(pid)));
            }

            try
            {
                m_CpuUsages[pid] = (int)(m_cpuUsageCounters[pid].NextValue() / processorCount);
            }
            catch (Exception ex)
            {
                m_cpuUsageCounters[pid].Dispose();
                m_cpuUsageCounters[pid] = null;
                m_cpuUsageCounters.Remove(pid);
                m_CpuUsages[pid] = 0;
                //Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }


            return m_CpuUsages[pid];
        }

        public static int GetTotalRequest(int pid)
        {
            if (!totalRequestCounters.ContainsKey(pid))
            {
                CreateAllRequestPerformanceCounter();
                if (!totalRequestCounters.ContainsKey(pid))
                {
                    return 0;
                }
            }

            var currentRequest = totalRequestCount.ContainsKey(pid) ? totalRequestCount[pid] : -1;

            try
            {
                totalRequestCount[pid] = (int)totalRequestCounters[pid].NextValue();
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

            return totalRequestCount.ContainsKey(pid) ? (currentRequest == -1 ? 0 : (totalRequestCount[pid] - currentRequest)) : 0;

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
                CreateAllRequestPerformanceCounter();
                if (!m_SuccessCounters.ContainsKey(pid))
                {
                    return 0;
                }
            }

            float incrementSuccessRequest = 0.0F;

            try
            {

                if (lastSuccess.ContainsKey(pid))
                {
                    float lastSuccessRequest = -1F;
                    lastSuccessRequest = lastSuccess[pid];
                    lastSuccess[pid] = m_SuccessCounters[pid].NextValue();
                    incrementSuccessRequest = lastSuccess[pid] - lastSuccessRequest;
                }
                else
                {
                    lastSuccess.Add(pid, m_SuccessCounters[pid].NextValue());
                }

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

            return (int)incrementSuccessRequest;
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
                CreateAllRequestPerformanceCounter();
                if (!m_FailedCounters.ContainsKey(pid))
                {
                    return 0;
                }
            }

            float incrementFailedRequest = 0.0F;

            try
            {

                if (lastFailed.ContainsKey(pid))
                {
                    float lastFailedRequest = lastFailed[pid];
                    lastFailed[pid] = m_FailedCounters[pid].NextValue();
                    incrementFailedRequest = lastFailed[pid] - lastFailedRequest;
                }
                else
                {
                    lastFailed.Add(pid, m_FailedCounters[pid].NextValue());
                }

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

            return (int)incrementFailedRequest;
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
                CreateAllRequestPerformanceCounter();
                if (!m_401Counters.ContainsKey(pid))
                {
                    return 0;
                }
            }

            float incrementNotAuthorizedRequest = 0.0F;

            try
            {

                if (last401.ContainsKey(pid))
                {
                    float lastFailedRequest = last401[pid];
                    last401[pid] = m_401Counters[pid].NextValue();
                    incrementNotAuthorizedRequest = last401[pid] - lastFailedRequest;
                }
                else
                {
                    last401.Add(pid, m_401Counters[pid].NextValue());
                }

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

            return (int)incrementNotAuthorizedRequest;
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
                CreateAllRequestPerformanceCounter();
                if (!m_404Counters.ContainsKey(pid))
                {
                    return 0;
                }
            }

            float incrementNotFoundRequest = 0.0F;

            try
            {

                if (last404.ContainsKey(pid))
                {
                    float lastFailedRequest = last404[pid];
                    last404[pid] = m_404Counters[pid].NextValue();
                    incrementNotFoundRequest = last404[pid] - lastFailedRequest;
                }
                else
                {
                    last404.Add(pid, m_404Counters[pid].NextValue());
                }

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

            return (int)incrementNotFoundRequest;
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
                CreateAllRequestPerformanceCounter();
                if (!m_500Counters.ContainsKey(pid))
                {
                    return 0;
                }
            }

            float incrementServerErrorRequest = 0.0F;

            try
            {

                if (last500.ContainsKey(pid))
                {
                    float lastFailedRequest = last500[pid];
                    last500[pid] = m_500Counters[pid].NextValue();
                    incrementServerErrorRequest = last500[pid] - lastFailedRequest;
                }
                else
                {
                    last500.Add(pid, m_500Counters[pid].NextValue());
                }

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

            return (int)incrementServerErrorRequest;
        }

        public static string GetInstanceName(Process process)
        {
            return GetProcessInstanceName(process.Id);
        }

        public static int GetCpuUsage(Process process)
        {
            return GetCpuUsage(process.Id);
        }





        public static void Dispose()
        {
            if (null != m_cpuUsageCounters)
            {
                foreach (var key in m_cpuUsageCounters.Keys)
                {
                    m_cpuUsageCounters[key].Dispose();
                }
                m_cpuUsageCounters.Clear();
            }

            if (null != totalRequestCounters)
            {
                foreach (var key in totalRequestCounters.Keys)
                {
                    totalRequestCounters[key].Dispose();
                }
                totalRequestCounters.Clear();
            }

            if (null != m_SuccessCounters)
            {
                foreach (var key in m_SuccessCounters.Keys)
                {
                    m_SuccessCounters[key].Dispose();
                }
                m_SuccessCounters.Clear();
            }

            if (null != m_FailedCounters)
            {
                foreach (var key in m_FailedCounters.Keys)
                {
                    m_FailedCounters[key].Dispose();
                }
                m_FailedCounters.Clear();
            }

            if (null != m_401Counters)
            {
                foreach (var key in m_401Counters.Keys)
                {
                    m_401Counters[key].Dispose();
                }
                m_401Counters.Clear();
            }

            if (null != m_404Counters)
            {
                foreach (var key in m_404Counters.Keys)
                {
                    m_404Counters[key].Dispose();
                }
                m_404Counters.Clear();
            }

            if (null != m_500Counters)
            {
                foreach (var key in m_500Counters.Keys)
                {
                    m_500Counters[key].Dispose();
                }
                m_500Counters.Clear();
            }

        }
        #endregion
    }
}