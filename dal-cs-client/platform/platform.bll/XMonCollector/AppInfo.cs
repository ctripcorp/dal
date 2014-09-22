using System;
using System.Collections.Generic;
using System.Web;
using System.Text;
using System.IO;
using System.Text.RegularExpressions;
using System.Diagnostics;
using System.Threading;

namespace Arch.Release.ServerAgent.Client.XMonCollector
{
    public sealed class AppInfo
    {

        private static AppInfo _instance = new AppInfo();

        public static AppInfo Instance { get { return _instance; } }

        //Windows server 2008及win7以后的机器，通过c:/windows/system32/inetsrv/appcmd.exe,可以获取iis的应用信息
        private readonly string appcmd = Environment.ExpandEnvironmentVariables("%COMSPEC%").Replace("cmd.exe", "inetsrv\\appcmd.exe");

        //Windows Server 2003及winxp以前的机器，通过c:/windows/system32/iisapp.vbs获取iis的应用信息
        private readonly string vbscmd = Environment.ExpandEnvironmentVariables("%COMSPEC%").Replace("cmd.exe", "iisapp.vbs");

        //Windows server 2008及win7以后的机器，输出结果为 WP "pid" (applicationPool:poolName)
        private Regex regex = new Regex("WP\\s\"(?<pid>[0-9]{1,})\"\\s\\(applicationPool:(?<poolName>.*?)\\)", RegexOptions.IgnoreCase);

        //Windows Server 2003及winxp以前的机器，输出结果为 PID: "pid" AppPoolId: poolName
        private Regex vbsRegex = new Regex("PID:\\s*\"?(?<pid>[0-9]{1,})\"?\\s*AppPoolId:\\s*(?<poolName>.*?)", RegexOptions.IgnoreCase);

        private Dictionary<int, string> w3wp = new Dictionary<int, string>();

        private Dictionary<int, AppEntity> apps = new Dictionary<int, AppEntity>();

        private StringBuilder processOutput = new StringBuilder();

        private ProcessStartInfo processStartInfo = null;

        private AppInfo()
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

        private void GetPidAndPoolName()
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
                Process appcmdProcess = System.Diagnostics.Process.Start(processStartInfo);

                while (!appcmdProcess.StandardOutput.EndOfStream)
                {
                    processOutput.Append(appcmdProcess.StandardOutput.ReadLine());
                }

                appcmdProcess.WaitForExit();
                appcmdProcess.Dispose();
                appcmdProcess.Close();

                if (File.Exists(appcmd))
                {
                    foreach (Match m in regex.Matches(processOutput.ToString()))
                    {
                        w3wp[int.Parse(m.Groups["pid"].Value)] = m.Groups["poolName"].Value;
                    }
                }
                else
                {
                    foreach (Match m in vbsRegex.Matches(processOutput.ToString()))
                    {
                        w3wp[int.Parse(m.Groups["pid"].Value)] = m.Groups["poolName"].Value;
                    }
                }
                processOutput.Remove(0, processOutput.Length);
                Thread.Sleep(1);
            }
            catch (Exception ex)
            {
                Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
            }
        }

        public Dictionary<int, AppEntity>.ValueCollection GetAllAppsInfo()
        {
            Process[] allProcesses = Process.GetProcessesByName("w3wp");

            if (allProcesses.Length != w3wp.Count)
            {
                GetPidAndPoolName();

                //将已经结束进程的Entity置为Null，for memory good
                int[] keys = new int[apps.Count];
                apps.Keys.CopyTo(keys, 0);
                for (int i = 0; i < keys.Length; i++)
                {
                    if (!w3wp.ContainsKey(keys[i]))
                    {
                        apps[keys[i]] = null;
                        apps.Remove(keys[i]);
                    }
                }
                keys = null;

                PerformanceCounterExtension.Instance.SynchronizeAllCounters(w3wp);
            }

            foreach (Process p in allProcesses)
            {
                try
                {
                    var pid = p.Id;

                    AppEntity currentEntity = null;
                    if (apps.ContainsKey(pid))
                    {
                        currentEntity = apps[pid];
                    }
                    else
                    {
                        currentEntity = new AppEntity();
                        apps[pid] = currentEntity;
                    }

                    //Get Pool Name
                    var poolName = string.Empty;
                    if (w3wp.ContainsKey(pid))
                    {
                        poolName = w3wp[pid];
                    }

                    //Organize the object
                    currentEntity.p = pid;
                    currentEntity.t = p.Threads.Count;
                    currentEntity.m = p.PrivateMemorySize64 / 1048576;
                    currentEntity.w = p.WorkingSet64 / 1048576;
                    currentEntity.v = p.VirtualMemorySize64 / 1048576;
                    currentEntity.c = PerformanceCounterExtension.Instance.GetCpuUsage(pid);
                    currentEntity.o = poolName;
                    currentEntity.q = PerformanceCounterExtension.Instance.GetTotalRequest(pid);
                    currentEntity.s = PerformanceCounterExtension.Instance.GetSuccessReqeust(pid);
                    currentEntity.f = PerformanceCounterExtension.Instance.GetFailedReqeust(pid);
                    currentEntity.u = PerformanceCounterExtension.Instance.GetNotAuthorizedReqeust(pid);
                    currentEntity.d = PerformanceCounterExtension.Instance.GetNotFoundReqeust(pid);
                    currentEntity.e = PerformanceCounterExtension.Instance.GetServerErrorReqeust(pid);
                }
                catch(Exception ex)
                {
                    Arch.Release.ServerAgent.Client.LogBroadcast.IISAgentLog.Error("{0}{1}", ex.Message, ex.StackTrace);
                }
            }

            return apps.Values;

        }


    }
}