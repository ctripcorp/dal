using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.IO;
using System.Data;
using System.Data.SqlClient;
//using platform.dao.log;
using System.ComponentModel;
using System.Text.RegularExpressions;
using System.Diagnostics;
using Microsoft.Web.Administration;
using System.Threading;
using System.Management;
using System.Runtime.InteropServices;

namespace platform.bll
{
    class Program
    {
        //[DllImport("XMonData.dll")]
        ////static extern double getCpuUsage();
        //static extern SummaryInterface createInstance();

        static void Main(string[] args)
        {

            //SummaryInterface iface = createInstance();

            List<Hello> l = new List<Hello>();
            l.Add(new Hello() { a = 1, b = 2 });


            Hello h = new Hello() { a=1, b=3};
            Console.WriteLine(l.Contains(h));

            Console.WriteLine(l.Find((entity) => { return entity.a == h.a; }).a);

            //CTimer.Run(SummaryInfo.GetMetrics, 1000);

            //SelectQuery query = new SelectQuery("SELECT IDProcess,ThreadCount,WorkingSet,PercentProcessorTime FROM Win32_PerfFormattedData_PerfProc_Process where Name = 'w3wp'");

            //SelectQuery query = new SelectQuery("SELECT Name from  IIsApplicationPools ");

            //using (var searcher = new ManagementObjectSearcher(query))
            //{
            //    foreach (var process in searcher.Get())
            //        using (process)
            //        {
            //            //var pid = process["IDProcess"].ToString();
            //            //var thread = process["ThreadCount"].ToString();
            //            //var size = process["WorkingSet"].ToString();
            //            //var time = process["PercentProcessorTime"].ToString();
            //            //Console.WriteLine(pid);
            //            //Console.WriteLine(thread);
            //            //Console.WriteLine(size);
            //            Console.WriteLine(process["Name"]);
            //        }
            //}

            //var data = new Dictionary<int, string>();
            //data.Add(744, "_LM_W3SVC_3_ROOT");
            //iface.AddProcess(744, "_LM_W3SVC_3_ROOT");
            //iface.SyncProcessList();

            while (Console.ReadKey().Key != ConsoleKey.Escape)
            {
                //Console.WriteLine(getCpuUsage());
               
                //Console.WriteLine(iface.GetCpuUsage());
                //Console.WriteLine(iface.GetAvailableMemory());
                //Console.WriteLine("Total: {0}", iface.GetRequestsTotal());
                //Console.WriteLine("Success: {0}", iface.GetRequestsSucceeded());
                //Console.WriteLine("Failed: {0}", iface.GetRequestsFailed());
                //Console.WriteLine("401: {0}", iface.GetRequestsNotAuthorized());
                //Console.WriteLine("404: {0}", iface.GetRequestsNotFound());
                //Console.WriteLine("500: {0}", iface.GetRequestsTimedOut());
                

                //Console.WriteLine("-------------------------------------");
                //Console.WriteLine(iface.GetRequestsTotalByPid(744));
                //Console.WriteLine(iface.GetRequestsSucceededByPid(744));
                //Console.WriteLine(iface.GetRequestsFailedByPid(744));
                //Console.WriteLine(iface.GetRequestsNotAuthorizedByPid(744));
                ////try
                ////{
                //Console.WriteLine(iface.GetRequestsNotFoundByPid(744));
                ////}
                ////catch { }

                //Console.WriteLine(iface.GetRequestsTimedOutByPid(744));
               
                //_LM_W3SVC_3_ROOT
                Console.WriteLine(SummaryInfo.AllInfo);
                Console.WriteLine(SummaryInfo.MachineInfo);
            }
        }

        //static void Main(string[] args)
        //{

        //    //var category = new PerformanceCounterCategory("ASP.NET Applications");

        //    //var instances = category.GetInstanceNames();

        //    //foreach (var instance in instances)
        //    //{
        //    //    Console.WriteLine(instance);
        //    //}

        //    //foreach(var a in System.Web.Hosting.ApplicationManager.GetApplicationManager().GetRunningApplications())
        //    //{
        //    //    Console.WriteLine(a.ID);
        //    //}

        //    //using (ServerManager serverManager = new ServerManager())
        //    //{
        //    //    Console.WriteLine(serverManager.WorkerProcesses.Count);
        //    //    foreach (var w in serverManager.WorkerProcesses)
        //    //    {
                    
        //    //        foreach (var a in w.ApplicationDomains)
        //    //        {
        //    //            Console.WriteLine(a.Id);
        //    //        }
        //    //    }
        //    //}

        //    Console.WriteLine("app_info".ToUpperInvariant());
            
        //    Console.Read();



        //}

        

     
    }

    class Hello : IEquatable<Hello>
    {
        public int a { get; set; }
        public int b { get; set; }



        //public int CompareTo(object obj)
        //{
        //    Hello real = (Hello)obj;
        //    return a - real.a;
        //}



        public bool Equals(Hello other)
        {
            return a == other.a;
        }
    }

    [ComImport]
    [Guid("C5B24E5D-175D-46BF-AE82-78A7A6969E80")]
    [InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
    interface SummaryInterface
    {
        [PreserveSig]
        double GetCpuUsage();
        [PreserveSig]
        double GetAvailableMemory();
        [PreserveSig]
        double GetRequestsTotal();
        [PreserveSig]
        double GetRequestsFailed();
        [PreserveSig]
        double GetRequestsSucceeded();
        [PreserveSig]
        double GetRequestsNotAuthorized();
        [PreserveSig]
        double GetRequestsNotFound();
        [PreserveSig]
        double GetRequestsTimedOut();

        [PreserveSig]
        void AddProcess(int pid, string instanceName);
        [PreserveSig]
        void SyncProcessList();
        [PreserveSig]
        double GetRequestsTotalByPid(int pid);
        [PreserveSig]
        double GetRequestsFailedByPid(int pid);
        [PreserveSig]
        double GetRequestsSucceededByPid(int pid);
        [PreserveSig]
        double GetRequestsNotAuthorizedByPid(int pid);
        [PreserveSig]
        double GetRequestsNotFoundByPid(int pid);
        [PreserveSig]
        double GetRequestsTimedOutByPid(int pid);
      

    }
}
