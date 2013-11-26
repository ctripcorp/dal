using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;
using System.Data;
using System.Data.SqlClient;
//using platform.dao.log;
using System.ComponentModel;
using System.Text.RegularExpressions;
using platform.dao.enums;
using System.Diagnostics;
using Microsoft.Web.Administration;

namespace platform.bll
{
    class Program
    {
        static void Main(string[] args)
        {

            //var category = new PerformanceCounterCategory("ASP.NET Applications");

            //var instances = category.GetInstanceNames();

            //foreach (var instance in instances)
            //{
            //    Console.WriteLine(instance);
            //}

            foreach(var a in System.Web.Hosting.ApplicationManager.GetApplicationManager().GetRunningApplications())
            {
                Console.WriteLine(a.ID);
            }

            using (ServerManager serverManager = new ServerManager())
            {
                Console.WriteLine(serverManager.WorkerProcesses.Count);
                foreach (var w in serverManager.WorkerProcesses)
                {
                    
                    foreach (var a in w.ApplicationDomains)
                    {
                        Console.WriteLine(a.Id);
                    }
                }
            }

       
            
            Console.Read();

        }

        

     
    }
}
