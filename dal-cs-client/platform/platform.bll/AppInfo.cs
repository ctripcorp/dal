using System;
using System.Collections.Generic;
using System.Web;
using System.Text;
using System.IO;
using System.Text.RegularExpressions;
using System.Diagnostics;
using System.Threading;
using System.Management;
using Microsoft.Web.Administration;

namespace platform.bll
{
    public sealed class AppInfo
    {

        private static Dictionary<int, string> w3wp = new Dictionary<int, string>();

        private static Dictionary<int, string> poolInsanceName = new Dictionary<int, string>();

        private static List<AppEntity> apps = new List<AppEntity>();

        public static List<AppEntity> Apps
        {
            get { return apps; }
        }

        static AppInfo()
        {
            GetPoolInstanceName();
        }

        private static void GetPoolInstanceName()
        {
            var keyCollection = new int[w3wp.Keys.Count];
            w3wp.Keys.CopyTo(keyCollection, 0);
            for (int i = 0; i < keyCollection.Length; i++)
            {
                w3wp[keyCollection[i]] = null;
            }
            w3wp.Clear();

            keyCollection = new int[poolInsanceName.Keys.Count];
            poolInsanceName.Keys.CopyTo(keyCollection, 0);
            for (int i = 0; i < keyCollection.Length; i++)
            {
                poolInsanceName[keyCollection[i]] = null;
            }
            poolInsanceName.Clear();

            using (ServerManager serverManager = new ServerManager())
            {
                foreach (var w in serverManager.WorkerProcesses)
                {

                    int pid = w.ProcessId;
                    w3wp[pid] = w.AppPoolName;
                    foreach (var a in w.ApplicationDomains)
                    {
                        poolInsanceName[pid] = a.Id.Replace("/", "_");
                    }
                }
            }
        }

        static SelectQuery query = new SelectQuery("SELECT ProcessId,ThreadCount,WorkingSetSize FROM Win32_Process where Name = 'w3wp.exe'");
        //static SelectQuery query = new SelectQuery("SELECT IDProcess,ThreadCount,WorkingSet,PercentProcessorTime FROM Win32_PerfFormattedData_PerfProc_Process where Name = 'w3wp'");
        public static void GetAllAppsInfo()
        {
            List<AppEntity> localEntities = new List<AppEntity>(w3wp.Count);

            try
            {
                using (var searcher = new ManagementObjectSearcher(query))
                {
                    foreach (var process in searcher.Get())
                        using (process)
                        {
                            try
                            {
                                var pid = process["ProcessId"].ToString();
                                var thread = process["ThreadCount"].ToString();
                                var size = process["WorkingSetSize"].ToString();
                                //var cpuTime = process["PercentProcessorTime"].ToString();
                                AppEntity currentEntity = new AppEntity();
                                currentEntity.p = int.Parse(pid);
                                currentEntity.t = int.Parse(thread);
                                currentEntity.w = long.Parse(size) / 1048576;
                                //currentEntity.c = cpuTime;
                                //currentEntity.c = PerformanceCounterExtension.GetCpuUsage(currentEntity.p);

                                localEntities.Add(currentEntity);
                            }
                            catch (Exception ex) {  }
                        }
                }
            }
            catch (Exception ex) {  }

            //bool getOrNot = true;
            //按照现状，w3wp的数量小于localEntities的数量，因此，如果localEntities中存在，而w3wp中不存在，则需要获取
            //if (w3wp.Count != localEntities.Count)
            //{
            //    GetPoolInstanceName();
            //    PerformanceCounterExtension.SynchronizeAllCounters(poolInsanceName);
            //}
            //else
            //{
            foreach (var currentEntity in localEntities)
            {
                if (!w3wp.ContainsKey(currentEntity.p))
                {
                    GetPoolInstanceName();
                    PerformanceCounterExtension.SynchronizeAllCounters(poolInsanceName);
                    break;
                    //GetPidAndPoolName();
                    //PerformanceCounterExtension.SynchronizeAllCounters(w3wp);
                }

            }
            //}

            foreach (var currentEntity in localEntities)
            {
                if (w3wp.ContainsKey(currentEntity.p))
                {
                    currentEntity.o = w3wp[currentEntity.p];
                }
                currentEntity.c = PerformanceCounterExtension.GetCpuUsage(currentEntity.p);
                currentEntity.q = PerformanceCounterExtension.GetTotalRequest(currentEntity.p);
                currentEntity.s = PerformanceCounterExtension.GetSuccessReqeust(currentEntity.p);
                currentEntity.f = PerformanceCounterExtension.GetFailedReqeust(currentEntity.p);
                currentEntity.u = PerformanceCounterExtension.GetNotAuthorizedReqeust(currentEntity.p);
                currentEntity.d = PerformanceCounterExtension.GetNotFoundReqeust(currentEntity.p);
                currentEntity.e = PerformanceCounterExtension.GetServerErrorReqeust(currentEntity.p);
            }

            Interlocked.Exchange(ref apps, localEntities);
            //Interlocked.Increment(ref _refreshW3wpProcesss);

        }


    }
}