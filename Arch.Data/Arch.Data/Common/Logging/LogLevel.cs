using System.ComponentModel;

namespace Arch.Data.Common.Logging
{
    /// <summary>
    /// 日志级别
    /// 不同的日志侦听器日志不同级别的日志
    /// </summary>
    public enum LogLevel
    {
        /// <summary>
        /// 信息
        /// </summary>
        [Description("信息")]
        Information = 0,

        /// <summary>
        /// 警告
        /// </summary>
        [Description("警告")]
        Warning = 1,

        /// <summary>
        /// 异常
        /// </summary>
        [Description("异常")]
        Error = 2,

        /// <summary>
        /// 严重异常
        /// </summary>
        [Description("严重异常")]
        Critical = 3
    }
}