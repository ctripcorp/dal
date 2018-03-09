using System;
using System.Configuration;

namespace Arch.Data.Common.Tracing.Configuration
{
    public sealed class TracingElement : ConfigurationElement
    {
        /// <summary>
        /// 名称
        /// </summary>
        private const String name = "name";

        /// <summary>
        /// 开关
        /// </summary>
        private const String turn = "turn";

        /// <summary>
        /// 名称,关键字
        /// </summary>
        [ConfigurationProperty(name, DefaultValue = "CentralLogging")]
        public String Name
        {
            get { return (String)this[name]; }
            set { this[name] = value; }
        }

        /// <summary>
        /// 开关
        /// </summary>
        [ConfigurationProperty(turn, DefaultValue = "on")]
        public String Turn
        {
            get { return (String)this[turn]; }
            set { this[turn] = value; }
        }

    }
}