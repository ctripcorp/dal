using System;
using System.Collections.Generic;
using System.Web;
using System.Net;
using System.Management;
using System.Diagnostics;
using System.Threading;
using System.Text;
using Microsoft.Win32;

namespace Arch.Release.ServerAgent.Client.XMonCollector
{
    public sealed class SummaryInfo
    {
        private static SummaryInfo _instance = new SummaryInfo();

        /// <summary>
        /// Default Instance of Summary Info
        /// </summary>
        public static SummaryInfo Instance { get { return _instance; } }

        #region Private Performance Counter

        private PerformanceCounter _cpuCounter;
        private PerformanceCounter _ramCounter;
        private PerformanceCounter _totalRequestCounter;

        //Status code >= 400
        private PerformanceCounter _failedRequestCounter;
        //Status code 200
        private PerformanceCounter _successRequestCounter;
        //Status code 401
        private PerformanceCounter _notAuthorizedRequestCounter;
        //Status code 404
        private PerformanceCounter _notFoundRequestCounter;
        //Status code 500
        private PerformanceCounter _serverErrorRequestCounter;

        #endregion

        #region Current Counter Value

        float _availablebytes = 0.0F;
        float _reqeustCount = 0.0F;
        float _currentCpu = 0.0F;
        float _failedRequest = 0.0F;
        float _successRequest = 0.0F;
        float _notAuthorizedRequest = 0.0F;
        float _notFoundRequest = 0.0F;
        float _serverErrorRequest = 0.0F;

        #endregion

        #region Last Counter Value

        float _lastRequestCount = 0.0F;
        float _lastFailedRequest = 0.0F;
        float _lastSuccessRequest = 0.0F;
        float _lastNotAuthorizedRequest = 0.0F;
        float _lastNotFoundRequest = 0.0F;
        float _lastServerErrorRequest = 0.0F;

        #endregion

        #region Other Private Fields

        private DateTime _beginTime;

        private long _totalPhysicalMem;

        private SummaryEntity _summaryEntity;

        private Dictionary<int, AppEntity>.ValueCollection _appEntities;

        private AllEntity _allEntity;

        //0 means not updated yet, 
        private Int32 _metricsUpdated = 0;

        private object _lockObj = new object();

        private string _onlyMachineInfo = null;
        private string _allInfo = null;

        private fastJSON.JSONParameters parameters = new fastJSON.JSONParameters();

        #endregion

        private SummaryInfo()
        {
            Bootstrap();
        }

        #region Private Methods
        private void Bootstrap()
        {
            _beginTime = new DateTime(1970, 1, 1, 0, 0, 0, 0);
            _totalPhysicalMem = GetPhysicalMemory();
            _summaryEntity = new SummaryEntity();
            _summaryEntity.i = GetIP();
            _summaryEntity.a = _totalPhysicalMem / 1048576;
            _summaryEntity.v = GetIISVersion();

            _allEntity = new AllEntity();

            //对于一下几个PerformanceCounter，不做异常检测，如果有一个Performance Counter不能获取，就没有提供服务的必要
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

            parameters.UsingGlobalTypes = false;
            parameters.UseExtensions = false;
        }

        private string GetIP()
        {
            string currentIp = string.Empty;
            try
            {
                System.Net.IPAddress addr;
                // 获得本机局域网IP地址
                addr = new System.Net.IPAddress(Dns.GetHostByName(Dns.GetHostName()).AddressList[0].Address);
                currentIp = addr.ToString();
            }
            catch
            {
                currentIp = "Error";
            }
            return currentIp;
        }

        private long GetPhysicalMemory()
        {
            long m_PhysicalMemory = 0L;

            try
            {
                ManagementClass mc = new ManagementClass("Win32_ComputerSystem");
                ManagementObjectCollection moc = mc.GetInstances();
                foreach (ManagementObject mo in moc)
                {
                    if (mo["TotalPhysicalMemory"] != null)
                    {
                        m_PhysicalMemory = long.Parse(mo["TotalPhysicalMemory"].ToString());
                        break;
                    }
                }
            }
            catch (Exception ex)
            {
                Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error(ex.Message);
            }
            return m_PhysicalMemory;
        }

        private int GetIISVersion()
        {
            try
            {
                //获取IIS版本号
                RegistryKey key = Registry.LocalMachine.OpenSubKey("software\\microsoft\\inetstp");
                int iis = (int)key.GetValue("majorversion", -1);

                return iis;
            }
            catch (Exception ex)
            {
                Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error(ex.Message);
            }
            return -1;
        }
        #endregion

        //确保只有一个线程访问
        public void  GetMetrics()
        {
            _availablebytes = _ramCounter.NextValue();
            _reqeustCount = _totalRequestCounter.NextValue();
            _currentCpu = _cpuCounter.NextValue();
            _successRequest = _successRequestCounter.NextValue();
            _failedRequest = _failedRequestCounter.NextValue();
            _notAuthorizedRequest = _notAuthorizedRequestCounter.NextValue();
            _notFoundRequest = _notFoundRequestCounter.NextValue();
            _serverErrorRequest = _serverErrorRequestCounter.NextValue();

            lock (_lockObj)
            {
                _summaryEntity.m = (int)(((double)(_totalPhysicalMem - _availablebytes) / (double)_totalPhysicalMem) * 100);
                _summaryEntity.c = (int)_currentCpu;
                _summaryEntity.t = (long)(DateTime.UtcNow.Subtract(_beginTime)).TotalSeconds;
                _summaryEntity.q = (int)(_reqeustCount - _lastRequestCount);
                _summaryEntity.s = (int)(_successRequest - _lastSuccessRequest);
                _summaryEntity.f = (int)(_failedRequest - _lastFailedRequest);
                _summaryEntity.u = (int)(_notAuthorizedRequest - _lastNotAuthorizedRequest);
                _summaryEntity.d = (int)(_notFoundRequest - _lastNotFoundRequest);
                _summaryEntity.e = (int)(_serverErrorRequest - _lastServerErrorRequest);

                _appEntities = AppInfo.Instance.GetAllAppsInfo();

                _allEntity.a = _appEntities;
                _allEntity.s = _summaryEntity;
            }

            _lastRequestCount = _reqeustCount;
            _lastSuccessRequest = _successRequest;
            _lastFailedRequest = _failedRequest;
            _lastNotAuthorizedRequest = _notAuthorizedRequest;
            _lastNotFoundRequest = _notFoundRequest;
            _lastServerErrorRequest = _serverErrorRequest;

            //Indicate that the data has changed
            Interlocked.Exchange(ref _metricsUpdated, 1);

        }

        /// <summary>
        /// Get only the machine information
        /// </summary>
        public string MachineInfo
        {
            get {

                //如果_onlyMachineInfo是Null，且_metricsUpdated没有更新，则一直等待，直到更新成功
                //if语句仅执行一次？效率？性能？
                if (null == _onlyMachineInfo)
                {
                    while (_metricsUpdated == 0)
                    {
                        Thread.Sleep(100);
                    }
                }
                
                //如果_metricsUpdated为1，需要lock并更新_onlyMachineInfo
                //否则，直接返回结果
                if (Interlocked.CompareExchange(ref _metricsUpdated, 0, 1) == 1)
                {
                    lock (_lockObj)
                    {
                        _onlyMachineInfo = fastJSON.JSON.Instance.ToJSON(_summaryEntity, parameters);
                        _allInfo = fastJSON.JSON.Instance.ToJSON(_allEntity, parameters);
                    }
                }

                return _onlyMachineInfo;
            }
        }

        /// <summary>
        /// Get all information
        /// </summary>
        public string AllInfo
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

                //如果_metricsUpdated为1，需要lock并更新_allInfo
                //否则，直接返回结果
                if (Interlocked.CompareExchange(ref _metricsUpdated, 0, 1) == 1)
                {
                    lock (_lockObj)
                    {
                        _onlyMachineInfo = fastJSON.JSON.Instance.ToJSON(_summaryEntity, parameters);
                        _allInfo = fastJSON.JSON.Instance.ToJSON(_allEntity, parameters);
                    }
                }

                return _allInfo;
            }
        }


    }
}