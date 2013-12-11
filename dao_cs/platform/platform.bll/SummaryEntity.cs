using System;
using System.Collections.Generic;
using System.Web;

namespace platform.bll
{
    public class SummaryEntity
    {
        /// <summary>
        /// 内存占用百分比，以整形显示，如占用了80%则显示为80
        /// </summary>
        public int m { get; set; }

        /// <summary>
        /// 内存总量，以MB显示
        /// </summary>
        public long a { get; set; }

        /// <summary>
        /// CPU占用率，以整形显示，如占用80%，则显示为80
        /// </summary>
        public int c { get; set; }

        /// <summary>
        /// 本机IP
        /// </summary>
        public string i { get; set; }

        /// <summary>
        /// 以Timestamp形式标识当前的时间(秒)
        /// </summary>
        public long t { get; set; }

        /// <summary>
        /// 自上次访问后，有多少访问增量
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

        /// <summary>
        /// iis版本，6或7等
        /// </summary>
        public int v { get; set; }

    }
}