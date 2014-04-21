using System;
using System.Collections.Generic;
using System.Web;
using System.Diagnostics;
using System.Threading;
using Microsoft.Web.Administration;

namespace Arch.Release.ServerAgent.Client.XMonCollector
{
    public sealed class PerformanceCounterExtension
    {

        private static PerformanceCounterExtension _instance = new PerformanceCounterExtension();

        private PerformanceCounterExtension()
        {

        }

        public static PerformanceCounterExtension Instance { get { return _instance; } }

        #region Private Var
        private Dictionary<int, PerformanceCounter> m_cpuUsageCounters = new Dictionary<int, PerformanceCounter>();
        private Dictionary<int, int> m_CpuUsages = new Dictionary<int, int>();

        private Dictionary<int, PerformanceCounter> totalRequestCounters = new Dictionary<int, PerformanceCounter>();
        private Dictionary<int, PerformanceCounter> m_SuccessCounters = new Dictionary<int, PerformanceCounter>();
        private Dictionary<int, PerformanceCounter> m_FailedCounters = new Dictionary<int, PerformanceCounter>();
        private Dictionary<int, PerformanceCounter> m_401Counters = new Dictionary<int, PerformanceCounter>();
        private Dictionary<int, PerformanceCounter> m_404Counters = new Dictionary<int, PerformanceCounter>();
        private Dictionary<int, PerformanceCounter> m_500Counters = new Dictionary<int, PerformanceCounter>();

        private Dictionary<int, int> totalRequestCount = new Dictionary<int, int>();
        private Dictionary<int, float> lastSuccess = new Dictionary<int, float>();
        private Dictionary<int, float> lastFailed = new Dictionary<int, float>();
        private Dictionary<int, float> last401 = new Dictionary<int, float>();
        private Dictionary<int, float> last404 = new Dictionary<int, float>();
        private Dictionary<int, float> last500 = new Dictionary<int, float>();

        private List<int> ignorePids = new List<int>();
        #endregion

        #region Private Method
        private string GetProcessInstanceName(int pid)
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
                Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }

            return result;

        }

        private void CreateAllRequestPerformanceCounter()
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

        #region Public methods

        public void SynchronizeAllCounters(Dictionary<int, string> w3wp)
        {
            ignorePids.Clear();

            int[] keys = new int[m_cpuUsageCounters.Count];
            m_cpuUsageCounters.Keys.CopyTo(keys, 0);

            for (int i = 0; i < keys.Length; i++)
            {
                if (!w3wp.ContainsKey(keys[i]))
                {
                    m_cpuUsageCounters[keys[i]].Close();
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
                    totalRequestCounters[keys[i]].Close();
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
                    m_SuccessCounters[keys[i]].Close();
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
                    m_FailedCounters[keys[i]].Close();
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
                    m_401Counters[keys[i]].Close();
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
                    m_404Counters[keys[i]].Close();
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
                    m_500Counters[keys[i]].Close();
                    m_500Counters[keys[i]] = null;
                    m_500Counters.Remove(keys[i]);
                    last500.Remove(keys[i]);
                }
            }

            keys = null;
        }

        public int GetCpuUsage(int pid)
        {
            if (!m_cpuUsageCounters.ContainsKey(pid))
            {
                m_cpuUsageCounters.Add(pid, new PerformanceCounter("Process", "% Processor Time", GetProcessInstanceName(pid)));
            }

            try
            {
                m_CpuUsages[pid] = (int)(m_cpuUsageCounters[pid].NextValue() / Environment.ProcessorCount);
            }
            catch (Exception ex)
            {
                m_cpuUsageCounters[pid].Close();
                m_cpuUsageCounters[pid] = null;
                m_cpuUsageCounters.Remove(pid);
                m_CpuUsages[pid] = 0;
                Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }


            return m_CpuUsages[pid];
        }

        public int GetTotalRequest(int pid)
        {
            if (!totalRequestCounters.ContainsKey(pid))
            {
                CreateAllRequestPerformanceCounter();
                if (!totalRequestCounters.ContainsKey(pid))
                {
                    return 0;
                }

                //var instanceName = string.Format("{0}_{1}", pid, poolName);
                //totalRequestCounters[pid] = new PerformanceCounter("W3SVC_W3WP", "Total HTTP Requests Served", instanceName);
                // totalRequestCounters[pid] = new PerformanceCounter("ASP.NET Applications", "Requests Total", instanceName);

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
                    totalRequestCounters[pid].Close();
                    totalRequestCounters[pid] = null;
                    totalRequestCounters.Remove(pid);
                    if (!ignorePids.Contains(pid))
                        ignorePids.Add(pid);
                }
                Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }

            return totalRequestCount.ContainsKey(pid) ? (currentRequest == -1 ? 0 : (totalRequestCount[pid] - currentRequest)) : 0;

        }

        /// <summary>
        /// 获取200的请求数
        /// </summary>
        /// <param name="pid"></param>
        /// <returns></returns>
        public int GetSuccessReqeust(int pid)
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
                m_SuccessCounters[pid].Close();
                m_SuccessCounters[pid] = null;
                m_SuccessCounters.Remove(pid);
                if (!ignorePids.Contains(pid))
                    ignorePids.Add(pid);
                Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }

            return (int)incrementSuccessRequest;
        }

        /// <summary>
        /// 获取>=400的请求数
        /// </summary>
        /// <param name="pid"></param>
        /// <returns></returns>
        public int GetFailedReqeust(int pid)
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
                m_FailedCounters[pid].Close();
                m_FailedCounters[pid] = null;
                m_FailedCounters.Remove(pid);
                if (!ignorePids.Contains(pid))
                    ignorePids.Add(pid);
                Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }

            return (int)incrementFailedRequest;
        }

        /// <summary>
        /// 获取401的请求数
        /// </summary>
        /// <param name="pid"></param>
        /// <returns></returns>
        public int GetNotAuthorizedReqeust(int pid)
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
                m_401Counters[pid].Close();
                m_401Counters[pid] = null;
                m_401Counters.Remove(pid);
                if (!ignorePids.Contains(pid))
                    ignorePids.Add(pid);
                Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }

            return (int)incrementNotAuthorizedRequest;
        }

        /// <summary>
        /// 获取404的请求数
        /// </summary>
        /// <param name="pid"></param>
        /// <returns></returns>
        public int GetNotFoundReqeust(int pid)
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
                m_404Counters[pid].Close();
                m_404Counters[pid] = null;
                m_404Counters.Remove(pid);
                if (!ignorePids.Contains(pid))
                    ignorePids.Add(pid);
                Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }

            return (int)incrementNotFoundRequest;
        }

        /// <summary>
        /// 获取500的请求数
        /// </summary>
        /// <param name="pid"></param>
        /// <returns></returns>
        public int GetServerErrorReqeust(int pid)
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
                m_500Counters[pid].Close();
                m_500Counters[pid] = null;
                m_500Counters.Remove(pid);
                if (!ignorePids.Contains(pid))
                    ignorePids.Add(pid);
                Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }

            return (int)incrementServerErrorRequest;
        }

        public string GetInstanceName(Process process)
        {
            return GetProcessInstanceName(process.Id);
        }

        public int GetCpuUsage(Process process)
        {
            return GetCpuUsage(process.Id);
        }

        #endregion


    }
}