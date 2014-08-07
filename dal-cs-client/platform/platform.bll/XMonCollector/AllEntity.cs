using System;
using System.Collections.Generic;
using System.Web;

namespace Arch.Release.ServerAgent.Client.XMonCollector
{
    /// <summary>
    /// 新版本协议：
    /// 对于运行在IIS7上的.net40的程序，获取数据情况良好，对于iis6或者.net20程序获取数据欠佳
    /// 
    /// {
    ///     "s":{                                   //SummaryInfo，机器的总体信息
    ///             "m":55,                     //MemoryOccupy， 当前占用内存的百分比
    ///             "a":8016,                  //AllMemory， 机器总内存（MB）
    ///             "c":5,                       //CPUPercentage， CPU占用率
    ///             "i":"172.16.155.151", //ip, 机器的IP
    ///             "t":1385364059,     //timestamp, 从 1970,01,01 00:00:00开始，过了多少秒
    ///             "q":0,                      //totalIncrementRequest, 自上次访问后，总共增加了多少个访问量
    ///             "s":0,                      //totalIncrementSuccessRequest, 自上次访问后，总共增加了多少个成功的访问量
    ///             "f":0,                      //totalIncrementFailedRequest, 自上次访问后，总共增加了多少个失败的访问量（所有大于等于400的HTTP错误）
    ///             "u":0,                      //totalIncrement401Request, 自上次访问后，总共增加了多少个401的访问量
    ///             "d":0,                      //totalIncrement404Request, 自上次访问后，总共增加了多少个404的访问量
    ///             "e":0,                      //totalIncrement500Request, 自上次访问后，总共增加了多少个500访问量
    ///             "v":7                       //iisVersion，IIS版本
    ///         },
    ///     "a":[                               //AppInfo, 每个应用的信息
    ///         {
    ///             "p":8648,               //pid, 进程标识
    ///             "t":28,                    //threadCount, IIS应用程序池的线程数
    ///             "m":83,                   //PrivateMemorySize64
    ///             "w":55,                    //WorkingSet64
    ///             "v":5356,               //VirtualWorkingSet64
    ///             "c":0,                      //cpu, CPU占用率
    ///             "o":"ASP.NET v4.0", //poolName, 应用程序池名
    ///             "q":0,                      //totalIncrementRequest, 自上次访问后，总共增加了多少个访问量
    ///             "s":0,                      //totalIncrementSuccessRequest, 自上次访问后，总共增加了多少个成功的访问量
    ///             "f":0,                      //totalIncrementFailedRequest, 自上次访问后，总共增加了多少个失败的访问量（所有大于等于400的HTTP错误）
    ///             "u":0,                      //totalIncrement401Request, 自上次访问后，总共增加了多少个401的访问量
    ///             "d":0,                      //totalIncrement404Request, 自上次访问后，总共增加了多少个404的访问量
    ///             "e":0                      //totalIncrement500Request, 自上次访问后，总共增加了多少个500访问量
    ///         }
    ///     ]
    /// }
    /// </summary>
    public class AllEntity
    {
        /// <summary>
        /// 当前机器的总信息
        /// </summary>
        public SummaryEntity s { get; set; }

        /// <summary>
        /// 当前机器上所有应用程序池的信息
        /// </summary>
        public Dictionary<int, AppEntity>.ValueCollection a { get; set; }

    }
}