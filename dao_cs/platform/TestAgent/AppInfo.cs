using System;
using System.Collections.Generic;
using System.Web;
using System.Text;
using System.IO;
using System.Text.RegularExpressions;
using System.Diagnostics;
using System.Threading;
using System.Management;

namespace TestAgent
{
    public sealed class AppInfo
    {

        //private static AppInfo _instance = new AppInfo();

        //public static AppInfo Instance { get { return _instance; } }

        //Windows server 2008及win7以后的机器，通过c:/windows/system32/inetsrv/appcmd.exe,可以获取iis的应用信息
        private static readonly string appcmd = Environment.ExpandEnvironmentVariables("%COMSPEC%").Replace("cmd.exe", "inetsrv\\appcmd.exe");

        //Windows Server 2003及winxp以前的机器，通过c:/windows/system32/iisapp.vbs获取iis的应用信息
        private static readonly string vbscmd = Environment.ExpandEnvironmentVariables("%COMSPEC%").Replace("cmd.exe", "iisapp.vbs");

        //Windows server 2008及win7以后的机器，输出结果为 WP "pid" (applicationPool:poolName)
        private static Regex regex = new Regex("WP\\s\"(?<pid>[0-9]{1,})\"\\s\\(applicationPool:(?<poolName>.*?)\\)", RegexOptions.IgnoreCase);

        //Windows Server 2003及winxp以前的机器，输出结果为 PID: "pid" AppPoolId: poolName
        private static Regex vbsRegex = new Regex("PID:\\s*\"?(?<pid>[0-9]{1,})\"?\\s*AppPoolId:\\s*(?<poolName>.*?)", RegexOptions.IgnoreCase);

        private static Dictionary<int, string> w3wp = new Dictionary<int, string>();

        //private static int _refreshW3wpProcesss = 30;

        //private static Dictionary<int, AppEntity> apps = new Dictionary<int, AppEntity>();
        private static List<AppEntity> apps = new List<AppEntity>();

        //private static StringBuilder processOutput = new StringBuilder();

        private static ProcessStartInfo processStartInfo = null;

        public static List<AppEntity> Apps
        {
            get { return apps; }
        }

        static AppInfo()
        {
            if (File.Exists(appcmd))
            {
                //Windows Server 2008, win7
                processStartInfo = new ProcessStartInfo(appcmd, "list WP");
            }
            else
            {
                //Windows server 2003, xp
                processStartInfo = new ProcessStartInfo(vbscmd);
            }

            processStartInfo.UseShellExecute = false;
            processStartInfo.RedirectStandardInput = true;
            processStartInfo.RedirectStandardError = true;
            processStartInfo.RedirectStandardOutput = true;
            processStartInfo.WindowStyle = ProcessWindowStyle.Hidden;
            processStartInfo.CreateNoWindow = true;

            GetPidAndPoolName();
        }

        private static void GetPidAndPoolName()
        {
            try
            {
                var keyCollection = new int[w3wp.Keys.Count];
                w3wp.Keys.CopyTo(keyCollection, 0);
                for (int i = 0; i < keyCollection.Length; i++)
                {
                    w3wp[keyCollection[i]] = null;
                }
                w3wp.Clear();
                using (Process appcmdProcess = System.Diagnostics.Process.Start(processStartInfo))
                {

                    StringBuilder sb = new StringBuilder();
                    while (!appcmdProcess.StandardOutput.EndOfStream)
                    {
                        sb.Append(appcmdProcess.StandardOutput.ReadLine());
                    }

                    appcmdProcess.WaitForExit();
                    appcmdProcess.Dispose();
                    //appcmdProcess.Close();

                    if (File.Exists(appcmd))
                    {
                        foreach (Match m in regex.Matches(sb.ToString()))
                        {
                            w3wp[int.Parse(m.Groups["pid"].Value)] = m.Groups["poolName"].Value;
                        }
                    }
                    else
                    {
                        foreach (Match m in vbsRegex.Matches(sb.ToString()))
                        {
                            w3wp[int.Parse(m.Groups["pid"].Value)] = m.Groups["poolName"].Value;
                        }
                    }
                }
                //processOutput.Remove(0, processOutput.Length);

                //Thread.Sleep(1);
            }
            catch (Exception ex)
            {
                //Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }
        }

        //static SelectQuery query = new SelectQuery("SELECT ProcessId,ThreadCount,WorkingSetSize FROM Win32_Process where Name = 'w3wp.exe'");
        static SelectQuery query = new SelectQuery("SELECT IDProcess,ThreadCount,WorkingSet,PercentProcessorTime FROM Win32_PerfFormattedData_PerfProc_Process where Name = 'w3wp'");
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
                                var pid = process["IDProcess"].ToString();
                                var thread = process["ThreadCount"].ToString();
                                var size = process["WorkingSet"].ToString();
                                var cpuTime = process["PercentProcessorTime"].ToString();
                                AppEntity currentEntity = new AppEntity();
                                currentEntity.p = int.Parse(pid);
                                currentEntity.t = int.Parse(thread);
                                currentEntity.w = long.Parse(size) / 1048576;
                                currentEntity.c = cpuTime;
                                //currentEntity.c = PerformanceCounterExtension.GetCpuUsage(currentEntity.p);

                                localEntities.Add(currentEntity);
                            }
                            catch (Exception ex) {  }
                        }
                }
            }
            catch (Exception ex) {  }

            bool getOrNot = true;
            foreach (var currentEntity in localEntities)
            {
                if (getOrNot && !w3wp.ContainsKey(currentEntity.p))
                {
                    getOrNot = false;
                    GetPidAndPoolName();
                    //PerformanceCounterExtension.SynchronizeAllCounters(w3wp);
                }
                if (w3wp.ContainsKey(currentEntity.p))
                {
                    currentEntity.o = w3wp[currentEntity.p];
                }
                //currentEntity.c = PerformanceCounterExtension.GetCpuUsage(currentEntity.p);
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