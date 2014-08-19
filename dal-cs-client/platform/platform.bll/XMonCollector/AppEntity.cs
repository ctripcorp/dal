using System;
using System.Collections.Generic;
using System.Web;

namespace Arch.Release.ServerAgent.Client.XMonCollector
{
    public class AppEntity
    {
        /// <summary>
        /// IIS应用程序池的进程ID
        /// </summary>
        public int p { get; set; }

        /// <summary>
        /// IIS应用程序池的线程数
        /// </summary>
        public int t { get; set; }

        /// <summary>
        /// IIS应用程序池的实际占用内存
        /// </summary>
        public long m { get; set; }

        /// <summary>
        /// IIS应用程序池的工作总内存
        /// </summary>
        public long w { get; set; }

        /// <summary>
        /// IIS应用程序池的虚拟内存
        /// </summary>
        public long v { get; set; }

        /// <summary>
        /// IIS应用程序池的进程CPU占用率
        /// </summary>
        public int c { get; set; }

        /// <summary>
        /// IIS应用程序池的名称
        /// </summary>
        public string o { get; set; }

        /// <summary>
        /// IIS应用程序池，自上次访问后，有多少访问增量
        /// </summary>
        public int q { get; set; }

        /// <summary>
        /// 成功的访问数
        /// </summary>
        public int s { get; set; }

        /// <summary>
        /// 失败的访问数
        /// </summary>
        public int f { get; set; }

        /// <summary>
        /// 401的增量
        /// </summary>
        public int u { get; set; }

        /// <summary>
        /// 404的增量
        /// </summary>
        public int d { get; set; }

        /// <summary>
        /// 500的增量
        /// </summary>
        public int e { get; set; }

    }
}