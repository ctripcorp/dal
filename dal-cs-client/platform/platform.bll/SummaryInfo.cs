using System;
using System.Collections.Generic;
using System.Web;
using System.Net;
using System.Management;
using System.Diagnostics;
using System.Threading;
using System.Text;
using Microsoft.Win32;
using System.Net.Sockets;

namespace platform.bll
{
    public sealed class SummaryInfo
    {
        #region Private Performance Counter

        private static PerformanceCounter _cpuCounter;
        private static PerformanceCounter _ramCounter;
        private static PerformanceCounter _totalRequestCounter;

        //Status code >= 400
        private static PerformanceCounter _failedRequestCounter;
        //Status code 200
        private static PerformanceCounter _successRequestCounter;
        //Status code 401
        private static PerformanceCounter _notAuthorizedRequestCounter;
        //Status code 404
        private static PerformanceCounter _notFoundRequestCounter;
        //Status code 500
        private static PerformanceCounter _serverErrorRequestCounter;

        #endregion

        #region Current Counter Value

        private static float _availablebytes = 0.0F;
        private static float _reqeustCount = 0.0F;
        private static float _currentCpu = 0.0F;
        private static float _failedRequest = 0.0F;
        private static float _successRequest = 0.0F;
        private static float _notAuthorizedRequest = 0.0F;
        private static float _notFoundRequest = 0.0F;
        private static float _serverErrorRequest = 0.0F;

        #endregion

        #region Other Private Fields

        private static DateTime _beginTime;

        private static long _totalPhysicalMem;

        private static SummaryEntity _summaryEntity;

        private static string ip;

        private static long totalMemory;

        private static int iisVersion;

        private static AllEntity _allEntity;

        //0 means not updated yet, 
        private static Int32 _metricsUpdated = 0;

        private static string _onlyMachineInfo = null;
        private static string _allInfo = null;

        private static fastJSON.JSONParameters parameters = new fastJSON.JSONParameters();

        #endregion

        static SummaryInfo()
        {
            Bootstrap();
        }

        #region Private Methods

        private static void Init()
        {
            //对于以下几个PerformanceCounter，不做异常检测，如果有一个Performance Counter不能获取，就没有提供服务的必要
            _cpuCounter = new PerformanceCounter("Processor", "% Processor Time", "_Total");
            _ramCounter = new PerformanceCounter("Memory", "Available Bytes");
            _totalRequestCounter = new PerformanceCounter("ASP.NET Applications", "Requests Total", "__Total__");

            //Status code >= 400
            _failedRequestCounter = new PerformanceCounter("ASP.NET Applications", "Requests Failed", "__Total__");
            //Status code 200
            _successRequestCounter = new PerformanceCounter("ASP.NET Applications", "Requests Succeeded", "__Total__");
            //Status code 401
            _notAuthorizedRequestCounter = new PerformanceCounter("ASP.NET Applications", "Requests Not Authorized", "__Total__");
            //Status code 404
            _notFoundRequestCounter = new PerformanceCounter("ASP.NET Applications", "Requests Not Found", "__Total__");
            //Status code 500
            _serverErrorRequestCounter = new PerformanceCounter("ASP.NET Applications", "Requests Timed Out", "__Total__");

        }

        private static void Bootstrap()
        {
            _beginTime = new DateTime(1970, 1, 1, 0, 0, 0, 0);
            _totalPhysicalMem = GetPhysicalMemory();
            _summaryEntity = new SummaryEntity();
            ip = GetIP();
            totalMemory = _totalPhysicalMem / 1048576;
            iisVersion = GetIISVersion();

            _allEntity = new AllEntity();

            Init();

            parameters.UsingGlobalTypes = false;
            parameters.UseExtensions = false;
        }

        private static string GetIP()
        {
            string currentIp = string.Empty;
            try
            {
                IPHostEntry host;
                host = Dns.GetHostEntry(Dns.GetHostName());
                foreach (IPAddress ip in host.AddressList)
                {
                    if (ip.AddressFamily == AddressFamily.InterNetwork)
                    {
                        currentIp = ip.ToString();
                        break;
                    }
                }
                host = null;
            }
            catch
            {
                currentIp = "Error";
            }
            return currentIp;
        }

        private static long GetPhysicalMemory()
        {
            //long m_PhysicalMemory = 8016*1048576L;
            long m_PhysicalMemory = 0L;

            try
            {
                using (ManagementClass mc = new ManagementClass("Win32_ComputerSystem"))
                {
                    foreach (var mo in mc.GetInstances())
                        using (mo)
                        {
                            if (mo["TotalPhysicalMemory"] != null)
                            {
                                m_PhysicalMemory = long.Parse(mo["TotalPhysicalMemory"].ToString());
                                break;
                            }
                        }
                }
            }
            catch (Exception ex)
            {
                //Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error(ex.Message);
            }
            return m_PhysicalMemory;
        }

        private static int GetIISVersion()
        {
            try
            {
                //获取IIS版本号
                using (RegistryKey key = Registry.LocalMachine.OpenSubKey("software\\microsoft\\inetstp"))
                {
                    int iis = (int)key.GetValue("majorversion", -1);

                    return iis;
                }
            }
            catch (Exception ex)
            {
                //Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error(ex.Message);
            }
            return -1;
        }
        #endregion

        //确保只有一个线程访问
        public static void GetMetrics()
        {
            try
            {
                _availablebytes = _ramCounter.NextValue();
                _currentCpu = _cpuCounter.NextValue();
                _reqeustCount = _totalRequestCounter.NextValue();
                _successRequest = _successRequestCounter.NextValue();
                _failedRequest = _failedRequestCounter.NextValue();
                _notAuthorizedRequest = _notAuthorizedRequestCounter.NextValue();
                _notFoundRequest = _notFoundRequestCounter.NextValue();
                _serverErrorRequest = _serverErrorRequestCounter.NextValue();

                AppInfo.GetAllAppsInfo();
            }
            catch (Exception ex)
            {//Swallow it, do nothing
                //Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error(ex.Message);
            }

            try
            {
                SummaryEntity tempEntity = new SummaryEntity();
                tempEntity.i = ip;
                tempEntity.a = totalMemory;
                tempEntity.v = iisVersion;
                tempEntity.m = (int)(((double)(_totalPhysicalMem - _availablebytes) / (double)_totalPhysicalMem) * 100);
                tempEntity.c = (int)_currentCpu;
                tempEntity.t = (long)(DateTime.UtcNow.Subtract(_beginTime)).TotalSeconds;
                tempEntity.q = (int)(_reqeustCount);
                tempEntity.s = (int)(_successRequest);
                tempEntity.f = (int)(_failedRequest);
                tempEntity.u = (int)(_notAuthorizedRequest);
                tempEntity.d = (int)(_notFoundRequest);
                tempEntity.e = (int)(_serverErrorRequest);

                Interlocked.Exchange(ref _summaryEntity, tempEntity);

                AllEntity tempAllEntity = new AllEntity();
                tempAllEntity.a = AppInfo.Apps;
                tempAllEntity.s = _summaryEntity;

                Interlocked.Exchange(ref _allEntity, tempAllEntity);
            }
            catch (Exception ex)
            {//Swallow it
                //Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error(ex.Message);
            }

            //Indicate that the data has changed
            Interlocked.Exchange(ref _metricsUpdated, 1);

        }

        /// <summary>
        /// Get only the machine information
        /// </summary>
        public static string MachineInfo
        {
            get
            {
                //如果_onlyMachineInfo是Null，且_metricsUpdated没有更新，则一直等待，直到更新成功
                //if语句仅执行一次？效率？性能？
                if (null == _onlyMachineInfo)
                {
                    while (_metricsUpdated == 0)
                    {
                        Thread.Sleep(100);
                    }
                }

                ////如果_metricsUpdated为1，需要lock并更新_onlyMachineInfo
                ////否则，直接返回结果
                if (Interlocked.CompareExchange(ref _metricsUpdated, 0, 1) == 1)
                {
                    string temp_onlyMachineInfo = fastJSON.JSON.Instance.ToJSON(_summaryEntity, parameters);
                    string temp_allInfo = fastJSON.JSON.Instance.ToJSON(_allEntity, parameters);
                    Interlocked.Exchange(ref _onlyMachineInfo, temp_onlyMachineInfo);
                    Interlocked.Exchange(ref _allInfo, temp_allInfo);
                }

                return _onlyMachineInfo;
                //return null;
            }
        }

        /// <summary>
        /// Get all information
        /// </summary>
        public static string AllInfo
        {
            get
            {

                //如果_onlyMachineInfo是Null，且_metricsUpdated没有更新，则一直等待，直到更新成功
                //if语句仅执行一次？效率？安全？
                if (null == _allInfo)
                {
                    while (_metricsUpdated == 0)
                    {
                        Thread.Sleep(100);
                    }
                }

                ////如果_metricsUpdated为1，需要lock并更新_allInfo
                ////否则，直接返回结果,这句能保证一个线程写，但是多个线程读_allInfo呢？
                if (Interlocked.CompareExchange(ref _metricsUpdated, 0, 1) == 1)
                {
                    string temp_onlyMachineInfo = fastJSON.JSON.Instance.ToJSON(_summaryEntity, parameters);
                    string temp_allInfo = fastJSON.JSON.Instance.ToJSON(_allEntity, parameters);
                    Interlocked.Exchange(ref _onlyMachineInfo, temp_onlyMachineInfo);
                    Interlocked.Exchange(ref _allInfo, temp_allInfo);
                }

                return _allInfo;
                //return null;
            }
        }


    }
}