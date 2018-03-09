using System;
using System.Configuration;

namespace Arch.Data.Common.Metrics.Configuration
{
    public sealed class MetricsElement : ConfigurationElement
    {
        /// <summary>
        /// 名称
        /// </summary>
        private const String name = "name";

        public MetricsElement() { }

        /// <summary>
        /// 名称,关键字
        /// </summary>
        [ConfigurationProperty(name, DefaultValue = "CentralLogging")]
        public String Name
        {
            get { return (String)this[name]; }
            set { this[name] = value; }
        }

    }
}